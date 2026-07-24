#!/usr/bin/env node

import { execFileSync } from "node:child_process";
import {
  mkdtempSync,
  readFileSync,
  rmSync,
  statSync,
} from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";
import { fileURLToPath } from "node:url";

const EXPECTED_TITLE =
  "Java ile Tasarım Desenleri — Uygulamalı Saha Rehberi";
const MINIMUM_PDF_BYTES = 100_000;
const MINIMUM_PAGE_COUNT = 80;
const MAXIMUM_PAGE_COUNT = 450;
const A4_WIDTH_POINTS = 595.28;
const A4_HEIGHT_POINTS = 841.89;
const A4_TOLERANCE_POINTS = 3;
const COMMAND_TIMEOUT_MILLISECONDS = 120_000;
const COMMAND_MAX_BUFFER_BYTES = 128 * 1024 * 1024;
const CHAPTER_TITLES = Object.freeze([
  "Factory Method",
  "Abstract Factory",
  "Builder",
  "Prototype",
  "Singleton",
  "Adapter",
  "Bridge",
  "Composite",
  "Decorator",
  "Facade",
  "Flyweight",
  "Proxy",
  "Chain of Responsibility",
  "Command",
  "Iterator",
  "Mediator",
  "Memento",
  "Observer",
  "State",
  "Strategy",
  "Template Method",
  "Visitor",
]);

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, "..");
const bookPath = path.join(repositoryRoot, "BOOK.md");
const errors = [];
const metrics = {};
let temporaryDirectory;

const normalizeText = (text) =>
  String(text)
    .normalize("NFC")
    .replaceAll("\u00ad", "")
    .replace(/\s+/gu, " ")
    .trim();

const countPhrase = (text, phrase) => {
  const normalizedText = normalizeText(text).toLocaleLowerCase("tr-TR");
  const normalizedPhrase = normalizeText(phrase).toLocaleLowerCase("tr-TR");
  let count = 0;
  let offset = 0;

  while (true) {
    const index = normalizedText.indexOf(normalizedPhrase, offset);
    if (index === -1) {
      return count;
    }
    count += 1;
    offset = index + normalizedPhrase.length;
  }
};

const commandErrorMessage = (command, error) => {
  if (error?.code === "ENOENT") {
    return `Gerekli Poppler aracı bulunamadı: ${command}`;
  }

  const detail = String(error?.stderr || error?.message || "bilinmeyen hata")
    .trim()
    .split(/\r?\n/u)
    .slice(-3)
    .join(" | ");
  return `${command} çalıştırılamadı${detail ? `: ${detail}` : ""}`;
};

const runCommand = (command, args, { encoding = "utf8" } = {}) => {
  try {
    return execFileSync(command, args, {
      encoding,
      env: {
        ...process.env,
        LANG: "C",
        LC_ALL: "C",
      },
      maxBuffer: COMMAND_MAX_BUFFER_BYTES,
      stdio: ["ignore", "pipe", "pipe"],
      timeout: COMMAND_TIMEOUT_MILLISECONDS,
      windowsHide: true,
    });
  } catch (error) {
    errors.push(commandErrorMessage(command, error));
    return null;
  }
};

const parsePdfInfo = (pdfInfo) => {
  const title = pdfInfo
    .match(/^Title:[ \t]*(.*?)[ \t]*$/mu)?.[1]
    ?.normalize("NFC");
  if (title !== EXPECTED_TITLE) {
    errors.push(
      "PDF başlığı beklenen metadata ile aynı değil: "
        + `beklenen "${EXPECTED_TITLE}", bulunan "${title || "(yok)"}"`,
    );
  }

  const pageCountText = pdfInfo.match(
    /^Pages:[ \t]*(\d+)[ \t]*$/mu,
  )?.[1];
  const pageCount = pageCountText ? Number(pageCountText) : Number.NaN;
  if (!Number.isInteger(pageCount)) {
    errors.push("pdfinfo çıktısında sayfa sayısı okunamadı");
  } else if (
    pageCount < MINIMUM_PAGE_COUNT
    || pageCount > MAXIMUM_PAGE_COUNT
  ) {
    errors.push(
      `PDF sayfa sayısı ${MINIMUM_PAGE_COUNT}–${MAXIMUM_PAGE_COUNT} `
        + `aralığında değil: ${pageCount}`,
    );
  } else {
    metrics.pageCount = pageCount;
  }

  const pageSizeMatch = pdfInfo.match(
    /^Page size:[ \t]*([\d.]+)[ \t]+x[ \t]+([\d.]+)[ \t]+pts\b.*$/mu,
  );
  if (!pageSizeMatch) {
    errors.push("pdfinfo çıktısında sayfa ölçüsü okunamadı");
    return;
  }

  const dimensions = [Number(pageSizeMatch[1]), Number(pageSizeMatch[2])]
    .sort((left, right) => left - right);
  const isA4 =
    Math.abs(dimensions[0] - A4_WIDTH_POINTS) <= A4_TOLERANCE_POINTS
    && Math.abs(dimensions[1] - A4_HEIGHT_POINTS) <= A4_TOLERANCE_POINTS;
  if (!isA4) {
    errors.push(
      "PDF sayfa ölçüsü A4 değil: "
        + `${pageSizeMatch[1]} x ${pageSizeMatch[2]} pt`,
    );
  } else {
    metrics.pageSize =
      `${Number(pageSizeMatch[1]).toFixed(2)} x `
      + `${Number(pageSizeMatch[2]).toFixed(2)} pt (A4)`;
  }
};

const inspectExtractedText = (text) => {
  const normalizedText = normalizeText(text);
  const comparableText = normalizedText.toLocaleLowerCase("en-US");
  const missingTitles = CHAPTER_TITLES.filter(
    (title) =>
      !comparableText.includes(
        normalizeText(title).toLocaleLowerCase("en-US"),
      ),
  );
  if (missingTitles.length) {
    errors.push(`PDF metninde eksik pattern bölümleri: ${missingTitles.join(", ")}`);
  }
  metrics.chapterTitleCount = CHAPTER_TITLES.length - missingTitles.length;

  try {
    const book = readFileSync(bookPath, "utf8");
    const expectedCardCount = countPhrase(book, "30 saniyelik kart");
    const actualCardCount = countPhrase(text, "30 saniyelik kart");
    if (expectedCardCount === 0) {
      errors.push("BOOK.md içinde beklenen “30 saniyelik kart” işareti bulunamadı");
    } else if (actualCardCount !== expectedCardCount) {
      errors.push(
        "“30 saniyelik kart” sayısı BOOK.md ile tutarlı değil: "
          + `beklenen ${expectedCardCount}, bulunan ${actualCardCount}`,
      );
    }
    metrics.cardCount = `${actualCardCount}/${expectedCardCount}`;
  } catch (error) {
    errors.push(`BOOK.md okunamadı: ${error.message}`);
  }

  const forbiddenContent = [
    {
      label: "Linux kullanıcı dizini (/home/)",
      pattern: /\/home\//iu,
    },
    {
      label: "macOS kullanıcı dizini (/Users/)",
      pattern: /\/Users\//iu,
    },
    {
      label: "Windows kullanıcı dizini (C:\\Users\\)",
      pattern: /[A-Z]:\\Users\\/iu,
    },
    {
      label: "Mermaid render hatası",
      pattern:
        /(?:Mermaid render (?:hatası|başarısız)|Diyagram \d+ render hatası|Bilinmeyen Mermaid hatası)/iu,
    },
    {
      label: "Mermaid sözdizimi hatası",
      pattern: /(?:Syntax error in text|UnknownDiagramError|Error rendering diagram)/iu,
    },
    {
      label: "şablon yer tutucusu",
      pattern: /\{\{(?:BOOK_BODY|MERMAID_SCRIPT)\}\}/u,
    },
    {
      label: "TODO/TBD yer tutucusu",
      pattern: /\b(?:TODO|TBD)\b/iu,
    },
    {
      label: "lorem ipsum yer tutucusu",
      pattern: /\blorem ipsum\b/iu,
    },
  ];

  for (const { label, pattern } of forbiddenContent) {
    if (pattern.test(normalizedText)) {
      errors.push(`PDF metninde yayınlanmaması gereken içerik bulundu: ${label}`);
    }
  }
};

const inspectInternalLinks = (xml, pageCount) => {
  const hrefPattern = /<a\b[^>]*\bhref=(["'])(.*?)\1/giu;
  const internalTargets = [];

  for (const match of xml.matchAll(hrefPattern)) {
    const href = match[2].replaceAll("&amp;", "&");
    const fragmentMatch = href.match(/#(?:page=?)?(\d+)\b/iu);
    if (!fragmentMatch) {
      continue;
    }

    const base = href.slice(0, fragmentMatch.index);
    if (/^[a-z][a-z\d+.-]*:/iu.test(base) || base.startsWith("//")) {
      continue;
    }

    const target = Number(fragmentMatch[1]);
    if (
      Number.isInteger(target)
      && target >= 1
      && (!Number.isInteger(pageCount) || target <= pageCount)
    ) {
      internalTargets.push(target);
    }
  }

  const uniqueTargets = new Set(internalTargets);
  if (internalTargets.length < 22 || uniqueTargets.size < 22) {
    errors.push(
      "PDF iç bağlantı ağı yetersiz: "
        + `${internalTargets.length} bağlantı, ${uniqueTargets.size} benzersiz sayfa hedefi`,
    );
  }
  metrics.internalLinks = internalTargets.length;
  metrics.uniqueLinkTargets = uniqueTargets.size;
};

const audit = (pdfArgument) => {
  const pdfPath = path.resolve(pdfArgument);
  let pdf;

  try {
    const fileStats = statSync(pdfPath);
    if (!fileStats.isFile()) {
      errors.push(`PDF yolu normal bir dosya değil: ${pdfPath}`);
      return;
    }
    pdf = readFileSync(pdfPath);
  } catch (error) {
    errors.push(`PDF okunamadı (${pdfPath}): ${error.message}`);
    return;
  }

  metrics.pdfPath = pdfPath;
  metrics.pdfBytes = pdf.length;
  if (pdf.length < MINIMUM_PDF_BYTES) {
    errors.push(
      `PDF beklenenden küçük: ${pdf.length} bayt `
        + `(alt sınır ${MINIMUM_PDF_BYTES} bayt)`,
    );
  }
  if (pdf.subarray(0, 5).toString("ascii") !== "%PDF-") {
    errors.push("Dosya geçerli bir %PDF- imzasıyla başlamıyor");
  }
  if (!pdf.includes(Buffer.from("/Outlines", "ascii"))) {
    errors.push("Ham PDF kataloğunda yer imi ağacı referansı (/Outlines) yok");
  }

  temporaryDirectory = mkdtempSync(path.join(tmpdir(), "pdf-portable-audit-"));
  const textPath = path.join(temporaryDirectory, "book.txt");
  const xmlPrefix = path.join(temporaryDirectory, "book");
  const xmlPath = `${xmlPrefix}.xml`;

  const pdfInfo = runCommand("pdfinfo", [pdfPath]);
  if (pdfInfo !== null) {
    parsePdfInfo(pdfInfo);
  }

  const textResult = runCommand(
    "pdftotext",
    ["-layout", "-enc", "UTF-8", pdfPath, textPath],
  );
  if (textResult !== null) {
    try {
      inspectExtractedText(readFileSync(textPath, "utf8"));
    } catch (error) {
      errors.push(`pdftotext çıktısı okunamadı: ${error.message}`);
    }
  }

  const xmlResult = runCommand(
    "pdftohtml",
    ["-q", "-xml", "-hidden", "-i", "-enc", "UTF-8", pdfPath, xmlPrefix],
  );
  if (xmlResult !== null) {
    try {
      inspectInternalLinks(readFileSync(xmlPath, "utf8"), metrics.pageCount);
    } catch (error) {
      errors.push(`pdftohtml XML çıktısı okunamadı: ${error.message}`);
    }
  }
};

const pdfArguments = process.argv.slice(2);
if (pdfArguments.length !== 1) {
  console.error("Kullanım: node scripts/audit-pdf-portable.mjs <pdf>");
  process.exitCode = 2;
} else {
  try {
    audit(pdfArguments[0]);
  } catch (error) {
    errors.push(`PDF denetimi beklenmedik biçimde durdu: ${error.message}`);
  } finally {
    if (temporaryDirectory) {
      rmSync(temporaryDirectory, { recursive: true, force: true });
    }
  }

  if (errors.length) {
    console.error("Taşınabilir PDF denetimi başarısız:");
    for (const error of errors) {
      console.error(`- ${error}`);
    }
    process.exitCode = 1;
  } else {
    console.log(`Taşınabilir PDF denetimi başarılı: ${metrics.pdfPath}`);
    console.log(
      `- ${metrics.pageCount} sayfa, ${metrics.pageSize}, `
        + `${metrics.pdfBytes} bayt`,
    );
    console.log(
      `- ${metrics.chapterTitleCount}/22 bölüm başlığı, `
        + `${metrics.cardCount} “30 saniyelik kart”`,
    );
    console.log(
      `- ${metrics.internalLinks} iç bağlantı, `
        + `${metrics.uniqueLinkTargets} benzersiz sayfa hedefi, /Outlines mevcut`,
    );
  }
}
