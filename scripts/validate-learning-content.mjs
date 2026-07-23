#!/usr/bin/env node

import { execFileSync } from "node:child_process";
import {
  existsSync,
  readFileSync,
  readdirSync,
  statSync,
} from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, "..");
const errors = [];
const warnings = [];
const canonicalPatterns = Object.freeze([
  { family: "CREATIONAL", slug: "factory-method" },
  { family: "CREATIONAL", slug: "abstract-factory" },
  { family: "CREATIONAL", slug: "builder" },
  { family: "CREATIONAL", slug: "prototype" },
  { family: "CREATIONAL", slug: "singleton" },
  { family: "STRUCTURAL", slug: "adapter" },
  { family: "STRUCTURAL", slug: "bridge" },
  { family: "STRUCTURAL", slug: "composite" },
  { family: "STRUCTURAL", slug: "decorator" },
  { family: "STRUCTURAL", slug: "facade" },
  { family: "STRUCTURAL", slug: "flyweight" },
  { family: "STRUCTURAL", slug: "proxy" },
  { family: "BEHAVIORAL", slug: "chain-of-responsibility" },
  { family: "BEHAVIORAL", slug: "command" },
  { family: "BEHAVIORAL", slug: "iterator" },
  { family: "BEHAVIORAL", slug: "mediator" },
  { family: "BEHAVIORAL", slug: "memento" },
  { family: "BEHAVIORAL", slug: "observer" },
  { family: "BEHAVIORAL", slug: "state" },
  { family: "BEHAVIORAL", slug: "strategy" },
  { family: "BEHAVIORAL", slug: "template-method" },
  { family: "BEHAVIORAL", slug: "visitor" },
]);
const canonicalSlugs = canonicalPatterns.map(({ slug }) => slug);
const manifestPath = path.join(repositoryRoot, "docs", "chapter-manifest.txt");
const manifestEntries = existsSync(manifestPath)
  ? readFileSync(manifestPath, "utf8")
      .split(/\r?\n/)
      .filter((line) => line.trim() && !line.trimStart().startsWith("#"))
      .map((line) => {
        const [slug, chapterPath, testPath, ...extra] = line
          .split("|")
          .map((field) => field.trim());
        if (!slug || !chapterPath || !testPath || extra.length) {
          errors.push(`docs/chapter-manifest.txt: geçersiz satır: ${line}`);
          return null;
        }
        return { chapterPath, slug, testPath };
      })
      .filter(Boolean)
  : [];

if (!existsSync(manifestPath)) {
  errors.push("docs/chapter-manifest.txt eksik");
}
if (manifestEntries.length !== 22) {
  errors.push(`Manifest içinde 22 pattern bekleniyordu, bulunan ${manifestEntries.length}`);
}
if (new Set(manifestEntries.map(({ slug }) => slug)).size !== manifestEntries.length) {
  errors.push("Manifest içinde yinelenen chapter slug bulundu");
}
if (
  new Set(manifestEntries.map(({ chapterPath }) => chapterPath)).size
  !== manifestEntries.length
) {
  errors.push("Manifest içinde yinelenen chapter dosya yolu bulundu");
}
if (
  new Set(manifestEntries.map(({ testPath }) => testPath)).size
  !== manifestEntries.length
) {
  errors.push("Manifest içinde yinelenen pattern test dosya yolu bulundu");
}

const manifestSlugs = manifestEntries.map(({ slug }) => slug);
if (JSON.stringify(manifestSlugs) !== JSON.stringify(canonicalSlugs)) {
  errors.push(
    "Manifest slug'ları kanonik 22 GoF envanteriyle aynı sıra ve içerikte değil",
  );
}

const chapterPaths = manifestEntries.map(({ chapterPath }) => chapterPath);

const walk = (directory) =>
  readdirSync(directory).flatMap((entry) => {
    const absolute = path.join(directory, entry);
    return statSync(absolute).isDirectory() ? walk(absolute) : [absolute];
  });

const inspectMarkdownFences = (relativePath, content) => {
  const blocks = [];
  let openFence = null;

  content.split(/\r?\n/).forEach((line, index) => {
    const match = line.match(/^ {0,3}(`{3,}|~{3,})(.*)$/);
    if (!match) {
      if (openFence) {
        openFence.lines.push(line);
      }
      return;
    }

    const marker = match[1];
    const suffix = match[2];
    if (!openFence) {
      if (marker.startsWith("`") && suffix.includes("`")) {
        errors.push(
          `${relativePath}:${index + 1}: backtick fence info string içinde backtick olamaz`,
        );
        return;
      }
      openFence = {
        language: suffix.trim().split(/\s+/, 1)[0].toLowerCase(),
        length: marker.length,
        lines: [],
        marker: marker[0],
        startLine: index + 1,
      };
      return;
    }

    const closesFence = marker[0] === openFence.marker
      && marker.length >= openFence.length
      && suffix.trim() === "";
    if (closesFence) {
      blocks.push({
        content: openFence.lines.join("\n"),
        language: openFence.language,
        startLine: openFence.startLine,
      });
      openFence = null;
    } else {
      openFence.lines.push(line);
    }
  });

  if (openFence) {
    errors.push(
      `${relativePath}:${openFence.startLine}: kapanmamış Markdown code fence`,
    );
  }

  return blocks;
};

const checkMermaidBlocks = (relativePath, blocks, { required = false } = {}) => {
  const mermaidBlocks = blocks.filter(({ language }) => language === "mermaid");
  if (required && mermaidBlocks.length === 0) {
    errors.push(`${relativePath}: Mermaid diyagramı yok`);
  }

  const supportedDirectives =
    /^\s*(?:flowchart|graph|sequenceDiagram|classDiagram|stateDiagram(?:-v2)?)\b/;
  mermaidBlocks.forEach(({ content, startLine }) => {
    if (!supportedDirectives.test(content)) {
      errors.push(
        `${relativePath}:${startLine}: Mermaid bloğu tanınan bir diagram bildirimiyle başlamıyor`,
      );
    }
  });
};

for (const relativePath of chapterPaths) {
  const absolutePath = path.join(repositoryRoot, relativePath);
  if (!existsSync(absolutePath)) {
    errors.push(`${relativePath}: bölüm dosyası eksik`);
    continue;
  }

  const content = readFileSync(absolutePath, "utf8");
  const lines = content.split(/\r?\n/);
  const h1Count = lines.filter((line) => /^# [^#]/.test(line)).length;
  const fencedBlocks = inspectMarkdownFences(relativePath, content);

  if (h1Count !== 1) {
    errors.push(`${relativePath}: tam bir H1 bekleniyordu, bulunan ${h1Count}`);
  }
  if (lines.length < 140) {
    errors.push(`${relativePath}: içerik çok kısa (${lines.length} satır, alt sınır 140)`);
  }
  checkMermaidBlocks(relativePath, fencedBlocks, { required: true });
  if (!/test/i.test(content)) {
    errors.push(`${relativePath}: test kontratlarını açıklayan bölüm yok`);
  }
  if (!/production/i.test(content)) {
    warnings.push(`${relativePath}: production ayrımı görünür değil`);
  }
  if (!/alıştırma/i.test(content)) {
    errors.push(`${relativePath}: alıştırma bölümü yok`);
  }
  if (!/^## Örnek haritası: temel → güçlendirilmiş → production$/m.test(content)) {
    errors.push(`${relativePath}: temel/güçlendirilmiş/production örnek haritası yok`);
  }
  if (/\bTODO\b|\bTBD\b|lorem ipsum/i.test(content)) {
    errors.push(`${relativePath}: placeholder metin bulundu`);
  }
  if (/\/Users\/|C:\\\\Users\\\\/.test(content)) {
    errors.push(`${relativePath}: makineye özel mutlak yol bulundu`);
  }
}

const readAndCheckAuxiliaryMarkdown = (relativePath) => {
  const absolutePath = path.join(repositoryRoot, relativePath);
  if (!existsSync(absolutePath)) {
    errors.push(`${relativePath}: yardımcı Markdown dosyası eksik`);
    return { blocks: [], content: "" };
  }

  const content = readFileSync(absolutePath, "utf8");
  const blocks = inspectMarkdownFences(relativePath, content);
  checkMermaidBlocks(relativePath, blocks);
  return { blocks, content };
};

readAndCheckAuxiliaryMarkdown("README.md");
const { content: frontmatter } =
  readAndCheckAuxiliaryMarkdown("docs/book-frontmatter.md");
const atlasHeading = "# 22 pattern atlası";
const atlasStart = frontmatter.indexOf(atlasHeading);
const atlasRemainder = atlasStart >= 0
  ? frontmatter.slice(atlasStart + atlasHeading.length)
  : "";
const nextH1Offset = atlasRemainder.search(/\n# /);
const atlasSection = nextH1Offset >= 0
  ? atlasRemainder.slice(0, nextH1Offset)
  : atlasRemainder;
const atlasSlugs = [
  ...atlasSection.matchAll(/\]\(#chapter-([a-z0-9-]+)\)/g),
].map((match) => match[1]);
if (atlasStart < 0) {
  errors.push("docs/book-frontmatter.md: 22 pattern atlası başlığı eksik");
}
if (JSON.stringify(atlasSlugs) !== JSON.stringify(canonicalSlugs)) {
  errors.push(
    "docs/book-frontmatter.md: pattern atlas linkleri manifestle aynı sıra ve içerikte değil",
  );
}

const testRoot = path.join(repositoryRoot, "src", "test", "java");
const allTestFiles = existsSync(testRoot)
  ? walk(testRoot).filter((file) => file.endsWith("Test.java"))
  : [];
if (!existsSync(testRoot)) {
  errors.push("src/test/java dizini eksik");
}
const testFiles = manifestEntries.map(({ testPath }) =>
  path.join(repositoryRoot, testPath),
);

for (const testFile of testFiles) {
  const relativePath = path.relative(repositoryRoot, testFile);
  if (!existsSync(testFile)) {
    errors.push(`${relativePath}: manifestteki pattern testi eksik`);
    continue;
  }
  const content = readFileSync(testFile, "utf8");
  if (!/@Nested/.test(content)) {
    errors.push(`${relativePath}: @Nested grubu yok`);
  }
  if (!/@DisplayName/.test(content)) {
    errors.push(`${relativePath}: @DisplayName yok`);
  }
  if (!/@Test/.test(content)) {
    errors.push(`${relativePath}: @Test yok`);
  }
  if (/@Disabled/.test(content)) {
    errors.push(`${relativePath}: devre dışı bırakılmış test bulundu`);
  }
}

const auxiliaryTestPaths = [
  "src/test/java/com/can/catalog/PatternCatalogTest.java",
  "src/test/java/com/can/MainTest.java",
];
for (const relativePath of auxiliaryTestPaths) {
  const absolutePath = path.join(repositoryRoot, relativePath);
  if (!existsSync(absolutePath)) {
    errors.push(`${relativePath}: yardımcı entegrasyon testi eksik`);
    continue;
  }

  const content = readFileSync(absolutePath, "utf8");
  if (!/@Nested/.test(content) || !/@DisplayName/.test(content) || !/@Test/.test(content)) {
    errors.push(`${relativePath}: okunabilir @Nested/@DisplayName/@Test yapısı eksik`);
  }
  if (/@Disabled/.test(content)) {
    errors.push(`${relativePath}: devre dışı bırakılmış test bulundu`);
  }
}

const expectedTestCount = testFiles.length + auxiliaryTestPaths.length;
if (allTestFiles.length > expectedTestCount) {
  warnings.push(
    `Manifest ve katalog dışındaki ek test sınıfları: ${allTestFiles.length - expectedTestCount}`,
  );
}

const catalogSourcePath = path.join(
  repositoryRoot,
  "src",
  "main",
  "java",
  "com",
  "can",
  "catalog",
  "PatternCatalog.java",
);
if (!existsSync(catalogSourcePath)) {
  errors.push("src/main/java/com/can/catalog/PatternCatalog.java: çalıştırılabilir katalog eksik");
} else {
  const catalogSource = readFileSync(catalogSourcePath, "utf8");
  const catalogEntries = [
    ...catalogSource.matchAll(
      /example\(\s*"([a-z0-9-]+)"\s*,\s*"[^"]+"\s*,\s*(CREATIONAL|STRUCTURAL|BEHAVIORAL)\b/g,
    ),
  ].map((match) => ({ family: match[2], slug: match[1] }));
  const catalogSlugs = catalogEntries.map(({ slug }) => slug);

  if (catalogSlugs.length !== 22 || new Set(catalogSlugs).size !== 22) {
    errors.push(
      `PatternCatalog içinde 22 benzersiz pattern kaydı bekleniyordu, bulunan ${catalogSlugs.length}`,
    );
  }
  if (JSON.stringify(catalogEntries) !== JSON.stringify(canonicalPatterns)) {
    errors.push(
      "PatternCatalog kayıtları kanonik envanterle aynı slug, aile ve sırada değil",
    );
  }
}

const directoryToFamily = new Map([
  ["creational", "CREATIONAL"],
  ["structural", "STRUCTURAL"],
  ["behavirol", "BEHAVIORAL"],
]);
const familyFromSourcePath = (relativePath) => {
  const match = relativePath
    .replaceAll("\\", "/")
    .match(/(?:^|\/)com\/can\/(creational|structural|behavirol)\//);
  return match ? directoryToFamily.get(match[1]) : null;
};
manifestEntries.forEach(({ chapterPath, slug, testPath }, index) => {
  const expectedFamily = canonicalPatterns[index]?.family;
  const chapterFamily = familyFromSourcePath(chapterPath);
  const testFamily = familyFromSourcePath(testPath);
  if (chapterFamily !== expectedFamily) {
    errors.push(
      `${chapterPath}: ${slug} için beklenen aile ${expectedFamily}, bulunan ${chapterFamily ?? "yok"}`,
    );
  }
  if (testFamily !== expectedFamily) {
    errors.push(
      `${testPath}: ${slug} için beklenen aile ${expectedFamily}, bulunan ${testFamily ?? "yok"}`,
    );
  }
});

const bookPath = path.join(repositoryRoot, "BOOK.md");
if (!existsSync(bookPath)) {
  errors.push("BOOK.md eksik; scripts/build-book.sh çalıştırılmalı");
} else {
  const book = readFileSync(bookPath, "utf8");
  const generatedChapters = [...book.matchAll(/<!-- generated-chapter:/g)].length;
  if (generatedChapters !== 22) {
    errors.push(`BOOK.md içinde 22 generated chapter bekleniyordu, bulunan ${generatedChapters}`);
  }
  inspectMarkdownFences("BOOK.md", book);
  try {
    execFileSync("/bin/bash", [path.join(repositoryRoot, "scripts", "build-book.sh"), "--check"], {
      cwd: repositoryRoot,
      encoding: "utf8",
      stdio: "pipe",
    });
  } catch {
    errors.push("BOOK.md kaynak bölümlerle senkron değil; scripts/build-book.sh çalıştırılmalı");
  }
}

if (warnings.length) {
  console.warn("Uyarılar:");
  warnings.forEach((warning) => console.warn(`- ${warning}`));
}

if (errors.length) {
  console.error("İçerik doğrulaması başarısız:");
  errors.forEach((error) => console.error(`- ${error}`));
  process.exit(1);
}

console.log(
  `İçerik doğrulaması başarılı: ${chapterPaths.length} bölüm, `
    + `${testFiles.length} pattern testi ve ${auxiliaryTestPaths.length} entegrasyon testi.`,
);
