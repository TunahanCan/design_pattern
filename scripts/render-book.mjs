#!/usr/bin/env node

import { execFileSync, spawn } from "node:child_process";
import {
  accessSync,
  constants,
  existsSync,
  mkdtempSync,
  readFileSync,
  renameSync,
  rmSync,
  statSync,
  writeFileSync,
} from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, "..");
const markdownPath = path.join(repositoryRoot, "BOOK.md");
const templatePath = path.join(repositoryRoot, "docs", "assets", "book-template.html");
const pdfPath = path.join(repositoryRoot, "docs", "design-patterns-java.pdf");
const htmlOutputPath = path.join(repositoryRoot, "docs", "design-patterns-java.html");
const htmlStagingPath = path.join(
  repositoryRoot,
  "docs",
  `.design-patterns-java.${process.pid}.html`,
);
const rawPdfStagingPath = path.join(
  repositoryRoot,
  "docs",
  `.design-patterns-java.${process.pid}.raw.pdf`,
);
const pdfStagingPath = path.join(
  repositoryRoot,
  "docs",
  `.design-patterns-java.${process.pid}.pdf`,
);
const temporaryDirectory = mkdtempSync(path.join(tmpdir(), "design-patterns-book-"));
const bodyPath = path.join(temporaryDirectory, "book-body.html");
const htmlPath = path.join(temporaryDirectory, "book.html");

const chromeCandidates = [
  process.env.CHROME_PATH,
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
  "/Applications/Chromium.app/Contents/MacOS/Chromium",
  "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
  "/usr/bin/google-chrome",
  "/usr/bin/google-chrome-stable",
  "/usr/bin/chromium",
  "/usr/bin/chromium-browser",
  process.env.PROGRAMFILES
    ? path.join(process.env.PROGRAMFILES, "Google", "Chrome", "Application", "chrome.exe")
    : null,
].filter(Boolean);

const nodeMajorVersion = Number(process.versions.node.split(".")[0]);
if (nodeMajorVersion < 22) {
  throw new Error(`PDF renderer Node.js 22+ gerektirir; bulunan ${process.versions.node}.`);
}

const delay = (milliseconds) =>
  new Promise((resolve) => setTimeout(resolve, milliseconds));

class CdpClient {
  constructor(url) {
    this.url = url;
    this.socket = null;
    this.nextId = 1;
    this.pending = new Map();
  }

  async connect() {
    this.socket = new WebSocket(this.url);
    await new Promise((resolve, reject) => {
      const timeout = setTimeout(
        () => reject(new Error(`CDP bağlantısı zaman aşımına uğradı: ${this.url}`)),
        15_000,
      );
      this.socket.addEventListener(
        "open",
        () => {
          clearTimeout(timeout);
          resolve();
        },
        { once: true },
      );
      this.socket.addEventListener(
        "error",
        () => {
          clearTimeout(timeout);
          reject(new Error(`CDP bağlantısı açılamadı: ${this.url}`));
        },
        { once: true },
      );
    });

    this.socket.addEventListener("message", (event) => {
      const message = JSON.parse(String(event.data));
      if (!message.id) {
        return;
      }
      const pending = this.pending.get(message.id);
      if (!pending) {
        return;
      }
      this.pending.delete(message.id);
      clearTimeout(pending.timeout);
      if (message.error) {
        pending.reject(
          new Error(`${pending.method}: ${message.error.message || "CDP hatası"}`),
        );
      } else {
        pending.resolve(message.result || {});
      }
    });

    const rejectPending = (reason) => {
      for (const [id, pending] of this.pending.entries()) {
        clearTimeout(pending.timeout);
        pending.reject(new Error(`${pending.method}: ${reason}`));
        this.pending.delete(id);
      }
    };
    this.socket.addEventListener("close", () => rejectPending("CDP bağlantısı kapandı"));
    this.socket.addEventListener("error", () => rejectPending("CDP bağlantı hatası"));
  }

  send(method, params = {}, timeoutMilliseconds = 45_000) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      return Promise.reject(new Error(`CDP bağlantısı açık değil: ${method}`));
    }
    const id = this.nextId++;
    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        this.pending.delete(id);
        reject(new Error(`CDP komutu zaman aşımına uğradı: ${method}`));
      }, timeoutMilliseconds);
      this.pending.set(id, { method, reject, resolve, timeout });
      this.socket.send(JSON.stringify({ id, method, params }));
    });
  }

  close() {
    if (this.socket && this.socket.readyState < WebSocket.CLOSING) {
      this.socket.close();
    }
  }
}

const fetchText = async (urls) => {
  const errors = [];
  for (const url of urls) {
    try {
      const response = await fetch(url, { signal: AbortSignal.timeout(30_000) });
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return await response.text();
    } catch (error) {
      errors.push(`${url}: ${error.message}`);
    }
  }
  throw new Error(`Mermaid indirilemedi:\n${errors.join("\n")}`);
};

const waitForFile = async (file, timeoutMilliseconds) => {
  const deadline = Date.now() + timeoutMilliseconds;
  while (Date.now() < deadline) {
    if (existsSync(file)) {
      return;
    }
    await delay(100);
  }
  throw new Error(`Dosya beklenirken zaman aşımı: ${file}`);
};

const commandIsAvailable = (command, args = ["--version"]) => {
  try {
    execFileSync(command, args, {
      cwd: repositoryRoot,
      encoding: "utf8",
      stdio: "ignore",
      timeout: 15_000,
    });
    return true;
  } catch {
    return false;
  }
};

const readCdpStream = async (client, handle) => {
  const chunks = [];
  try {
    while (true) {
      const chunk = await client.send(
        "IO.read",
        { handle, size: 1_048_576 },
        30_000,
      );
      if (chunk.data) {
        chunks.push(
          Buffer.from(chunk.data, chunk.base64Encoded ? "base64" : "utf8"),
        );
      }
      if (chunk.eof) {
        return Buffer.concat(chunks);
      }
    }
  } finally {
    try {
      await client.send("IO.close", { handle }, 2_000);
    } catch {
      // Stream tamamlandıktan sonra browser kapanırsa handle zaten serbest kalır.
    }
  }
};

const renderPdfWithChrome = async ({ chrome, htmlPath, pdfPath, profilePath }) => {
  let chromeLog = "";
  let browserClient;
  let pageClient;
  const chromeProcess = spawn(
    chrome,
    [
      "--headless=new",
      "--remote-debugging-port=0",
      "--disable-gpu",
      "--disable-dev-shm-usage",
      "--disable-extensions",
      "--disable-background-networking",
      "--disable-component-update",
      "--disable-default-apps",
      "--disable-sync",
      "--no-first-run",
      "--no-default-browser-check",
      "--run-all-compositor-stages-before-draw",
      "--disable-features=AutofillServerCommunication,MediaRouter,OptimizationHints,Translate",
      `--user-data-dir=${profilePath}`,
      "about:blank",
    ],
    { cwd: repositoryRoot, stdio: ["ignore", "ignore", "pipe"] },
  );
  chromeProcess.stderr.on("data", (chunk) => {
    chromeLog = `${chromeLog}${chunk}`.slice(-12_000);
  });

  try {
    const portFile = path.join(profilePath, "DevToolsActivePort");
    const processFailure = new Promise((_, reject) => {
      chromeProcess.once("error", (error) => {
        reject(new Error(`Chrome başlatılamadı: ${error.message}`));
      });
      chromeProcess.once("exit", (code, signal) => {
        if (!existsSync(portFile)) {
          reject(
            new Error(
              `Chrome DevTools başlamadan kapandı (code=${code}, signal=${signal}).\n${chromeLog}`,
            ),
          );
        }
      });
    });
    await Promise.race([waitForFile(portFile, 20_000), processFailure]);
    const [port, browserSocketPath] = readFileSync(portFile, "utf8")
      .trim()
      .split(/\r?\n/);
    if (!port || !browserSocketPath) {
      throw new Error(`Chrome DevTools port bilgisi geçersiz:\n${chromeLog}`);
    }

    browserClient = new CdpClient(`ws://127.0.0.1:${port}${browserSocketPath}`);
    await browserClient.connect();
    const targetResult = await browserClient.send("Target.createTarget", {
      url: pathToFileURL(htmlPath).href,
    });

    let pageTarget;
    const targetDeadline = Date.now() + 15_000;
    while (Date.now() < targetDeadline && !pageTarget) {
      const response = await fetch(`http://127.0.0.1:${port}/json/list`, {
        signal: AbortSignal.timeout(5_000),
      });
      const targets = await response.json();
      pageTarget = targets.find((target) => target.id === targetResult.targetId);
      if (!pageTarget) {
        await delay(100);
      }
    }
    if (!pageTarget?.webSocketDebuggerUrl) {
      throw new Error(`PDF sayfa hedefi oluşturulamadı:\n${chromeLog}`);
    }

    pageClient = new CdpClient(pageTarget.webSocketDebuggerUrl);
    await pageClient.connect();
    await Promise.all([
      pageClient.send("Page.enable"),
      pageClient.send("Runtime.enable"),
    ]);

    let renderStatus = "";
    const renderDeadline = Date.now() + 90_000;
    while (Date.now() < renderDeadline) {
      const evaluation = await pageClient.send("Runtime.evaluate", {
        expression:
          "document.readyState + '|' + (document.body?.dataset.renderStatus || '')",
        returnByValue: true,
      });
      const value = evaluation.result?.value || "";
      [, renderStatus = ""] = String(value).split("|");
      if (renderStatus) {
        break;
      }
      await delay(150);
    }
    if (!renderStatus) {
      throw new Error(`HTML/Mermaid render zaman aşımı:\n${chromeLog}`);
    }
    if (renderStatus !== "ok") {
      const details = await pageClient.send("Runtime.evaluate", {
        expression:
          "[document.body.dataset.renderError || '', ...Array.from(document.querySelectorAll('.mermaid-error')).map((node) => node.textContent)].filter(Boolean).join('\\n\\n')",
        returnByValue: true,
      });
      throw new Error(
        `Mermaid render başarısız: ${renderStatus}\n${details.result?.value || chromeLog}`,
      );
    }

    await pageClient.send("Emulation.setEmulatedMedia", { media: "print" });
    const layoutResult = await pageClient.send(
      "Runtime.evaluate",
      {
        expression: `(async () => {
          await document.fonts.ready;
          await new Promise((resolve) =>
            requestAnimationFrame(() => requestAnimationFrame(resolve))
          );
          const diagrams = document.querySelectorAll(".mermaid").length;
          const renderedDiagrams = document.querySelectorAll(".mermaid svg").length;
          const localLinks = Array.from(document.querySelectorAll("a[href]"))
            .filter((link) => {
              const rawHref = link.getAttribute("href") || "";
              return !rawHref.startsWith("#") && link.href.startsWith("file:");
            })
            .length;
          const chapterLinks = Array.from(document.querySelectorAll("a[href]"))
            .filter((link) => (link.getAttribute("href") || "").startsWith("#chapter-"))
            .length;
          const overflow = Array.from(
            document.querySelectorAll("pre, table, .mermaid")
          )
            .filter(
              (element) =>
                element.scrollWidth > element.clientWidth + 2 ||
                element.scrollHeight > element.clientHeight + 2
            )
            .slice(0, 10)
            .map((element) => ({
              tag: element.tagName,
              className: element.className,
              text: (element.textContent || "").trim().slice(0, 80),
            }));
          return { diagrams, renderedDiagrams, localLinks, chapterLinks, overflow };
        })()`,
        awaitPromise: true,
        returnByValue: true,
      },
      30_000,
    );
    const layout = layoutResult.result?.value;
    if (!layout || layout.diagrams < 1 || layout.renderedDiagrams !== layout.diagrams) {
      throw new Error(
        `Mermaid sayısı tutarsız: ${JSON.stringify(layout || {})}`,
      );
    }
    if (layout.localLinks) {
      throw new Error(`PDF HTML'inde ${layout.localLinks} yerel file:// bağlantısı kaldı.`);
    }
    if (layout.chapterLinks !== 22) {
      throw new Error(
        `PDF HTML'inde 22 bölüm bağlantısı bekleniyordu: ${JSON.stringify(layout)}`,
      );
    }
    if (layout.overflow?.length) {
      throw new Error(
        `PDF HTML'inde taşan bileşen bulundu:\n${JSON.stringify(layout.overflow, null, 2)}`,
      );
    }

    let result;
    try {
      result = await pageClient.send(
        "Page.printToPDF",
        {
          displayHeaderFooter: false,
          generateDocumentOutline: true,
          generateTaggedPDF: true,
          preferCSSPageSize: true,
          printBackground: true,
          // Büyük kitaplarda tek bir Base64 WebSocket frame'i istemci sınırını
          // aşabilir. Stream modu PDF'i sabit boyutlu CDP parçalarıyla taşır.
          transferMode: "ReturnAsStream",
        },
        300_000,
      );
    } catch (error) {
      throw new Error(
        `${error.message}\nChrome print log:\n${chromeLog || "(boş)"}`,
        { cause: error },
      );
    }
    if (!result.stream) {
      throw new Error("Chrome PDF stream handle'ı döndürmedi.");
    }
    writeFileSync(pdfPath, await readCdpStream(pageClient, result.stream));

    try {
      await browserClient.send("Browser.close", {}, 2_000);
    } catch {
      // Browser.close bağlantıyı yanıt dönmeden kapatabilir; PDF zaten yazıldı.
    }
  } finally {
    pageClient?.close();
    browserClient?.close();
    if (chromeProcess.exitCode === null) {
      chromeProcess.kill("SIGTERM");
      await Promise.race([
        new Promise((resolve) => chromeProcess.once("exit", resolve)),
        delay(3_000),
      ]);
    }
    if (chromeProcess.exitCode === null) {
      chromeProcess.kill("SIGKILL");
    }
  }
};

try {
  execFileSync("/bin/bash", [path.join(scriptDirectory, "build-book.sh")], {
    cwd: repositoryRoot,
    encoding: "utf8",
    stdio: "inherit",
  });
  execFileSync(process.execPath, [path.join(scriptDirectory, "validate-learning-content.mjs")], {
    cwd: repositoryRoot,
    encoding: "utf8",
    stdio: "inherit",
  });

  execFileSync(
    "npx",
    [
      "--yes",
      "marked@18.0.6",
      "--input",
      markdownPath,
      "--output",
      bodyPath,
      "--gfm",
    ],
    {
      cwd: repositoryRoot,
      encoding: "utf8",
      stdio: "inherit",
    },
  );

  const mermaidScript = await fetchText([
    "https://cdn.jsdelivr.net/npm/mermaid@11.12.0/dist/mermaid.min.js",
    "https://unpkg.com/mermaid@11.12.0/dist/mermaid.min.js",
  ]);
  const body = readFileSync(bodyPath, "utf8");
  const template = readFileSync(templatePath, "utf8");

  const html = template
    .replace("{{BOOK_BODY}}", () => body)
    .replace("{{MERMAID_SCRIPT}}", () => mermaidScript);
  writeFileSync(htmlPath, html, "utf8");
  if (process.env.BOOK_DEBUG_HTML) {
    writeFileSync(path.resolve(process.env.BOOK_DEBUG_HTML), html, "utf8");
  }

  const chrome = chromeCandidates.find((candidate) => {
    try {
      accessSync(candidate, constants.X_OK);
      return statSync(candidate).isFile();
    } catch {
      return false;
    }
  });
  if (!chrome) {
    throw new Error("PDF üretimi için desteklenen Chrome/Chromium kurulumu bulunamadı.");
  }

  const chromeProfile = path.join(temporaryDirectory, "chrome-profile");
  await renderPdfWithChrome({
    chrome,
    htmlPath,
    pdfPath: rawPdfStagingPath,
    profilePath: chromeProfile,
  });

  const rawPdf = readFileSync(rawPdfStagingPath);
  if (
    rawPdf.length < 100_000 ||
    rawPdf.subarray(0, 5).toString("ascii") !== "%PDF-"
  ) {
    throw new Error(
      `Üretilen PDF geçersiz veya beklenenden küçük: ${rawPdf.length} bayt`,
    );
  }

  writeFileSync(htmlStagingPath, html, "utf8");
  renameSync(htmlStagingPath, htmlOutputPath);
  console.log(
    `Etkileşimli HTML üretildi: ${path.relative(repositoryRoot, htmlOutputPath)} `
      + `(${Buffer.byteLength(html, "utf8")} bayt)`,
  );

  let finalizedPdfPath;
  if (process.platform === "darwin" && commandIsAvailable("swift")) {
    execFileSync(
      "swift",
      [
        path.join(scriptDirectory, "add-pdf-outline.swift"),
        rawPdfStagingPath,
        pdfStagingPath,
        path.join(repositoryRoot, "docs", "chapter-manifest.txt"),
      ],
      {
        cwd: repositoryRoot,
        encoding: "utf8",
        stdio: "inherit",
        timeout: 120_000,
      },
    );

    execFileSync(
      "swift",
      [path.join(scriptDirectory, "audit-pdf.swift"), pdfStagingPath],
      {
        cwd: repositoryRoot,
        encoding: "utf8",
        stdio: "inherit",
        timeout: 120_000,
      },
    );
    finalizedPdfPath = pdfStagingPath;
  } else {
    execFileSync(
      process.execPath,
      [path.join(scriptDirectory, "audit-pdf-portable.mjs"), rawPdfStagingPath],
      {
        cwd: repositoryRoot,
        encoding: "utf8",
        stdio: "inherit",
        timeout: 180_000,
      },
    );
    finalizedPdfPath = rawPdfStagingPath;
  }

  const pdf = readFileSync(finalizedPdfPath);
  renameSync(finalizedPdfPath, pdfPath);

  console.log(`PDF üretildi: ${path.relative(repositoryRoot, pdfPath)} (${pdf.length} bayt)`);
} finally {
  rmSync(htmlStagingPath, { force: true });
  rmSync(rawPdfStagingPath, { force: true });
  rmSync(pdfStagingPath, { force: true });
  rmSync(temporaryDirectory, { recursive: true, force: true });
}
