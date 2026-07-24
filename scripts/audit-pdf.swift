#!/usr/bin/env swift

import Foundation
import PDFKit

guard CommandLine.arguments.count == 2 else {
    FileHandle.standardError.write(Data("Kullanım: audit-pdf.swift <input.pdf>\n".utf8))
    exit(2)
}

let inputPath = CommandLine.arguments[1]
let inputURL = URL(fileURLWithPath: inputPath)

guard let document = PDFDocument(url: inputURL) else {
    FileHandle.standardError.write(Data("PDF açılamadı: \(inputPath)\n".utf8))
    exit(3)
}

let expectedChapterTokens = [
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
]

var failures: [String] = []
var warnings: [String] = []
var fullText = ""
var blankPages: [Int] = []
var localLinkPages: [Int] = []
var internalLinkCount = 0
var internalLinkTargetPages = Set<Int>()

let expectedTitle = "Java ile Tasarım Desenleri — Uygulamalı Saha Rehberi"
let documentTitle =
    document.documentAttributes?[PDFDocumentAttribute.titleAttribute] as? String
if documentTitle != expectedTitle {
    failures.append(
        "PDF metadata başlığı beklenen değerle eşleşmiyor: \(documentTitle ?? "<boş>")"
    )
}

if document.pageCount < 80 {
    failures.append("Beklenenden az sayfa var: \(document.pageCount)")
}
if document.pageCount > 450 {
    warnings.append("PDF çok uzun: \(document.pageCount) sayfa")
}

for index in 0..<document.pageCount {
    guard let page = document.page(at: index) else {
        failures.append("Sayfa okunamadı: \(index + 1)")
        continue
    }

    let pageText = page.string ?? ""
    let trimmed = pageText.trimmingCharacters(in: .whitespacesAndNewlines)
    fullText.append(pageText)
    fullText.append("\n")

    if index > 0 && trimmed.isEmpty {
        blankPages.append(index + 1)
    }

    let normalized = trimmed.lowercased(with: Locale(identifier: "tr_TR"))
    if normalized.contains("diyagram render hatası")
        || normalized.contains("mermaid render başarısız")
        || normalized.contains("lorem ipsum")
        || normalized.contains("tbd")
    {
        failures.append("Sayfa \(index + 1): placeholder/render hata metni bulundu")
    }

    let mediaBox = page.bounds(for: .mediaBox)
    if mediaBox.width <= 0 || mediaBox.height <= 0 {
        failures.append("Sayfa \(index + 1): geçersiz media box")
    }

    if let selection = page.selection(for: mediaBox) {
        for lineSelection in selection.selectionsByLine() {
            let bounds = lineSelection.bounds(for: page)
            let tolerance: CGFloat = 1.0
            if bounds.minX < mediaBox.minX - tolerance
                || bounds.maxX > mediaBox.maxX + tolerance
                || bounds.minY < mediaBox.minY - tolerance
                || bounds.maxY > mediaBox.maxY + tolerance
            {
                failures.append("Sayfa \(index + 1): sayfa dışına taşan metin kutusu")
                break
            }
        }
    }

    for annotation in page.annotations {
        if let goToAction = annotation.action as? PDFActionGoTo {
            internalLinkCount += 1
            if let targetPage = goToAction.destination.page {
                internalLinkTargetPages.insert(document.index(for: targetPage) + 1)
            }
            continue
        }
        guard let urlAction = annotation.action as? PDFActionURL,
              let url = urlAction.url else {
            continue
        }
        let rawURL = url.absoluteString
        if url.isFileURL
            || rawURL.contains("/Users/")
            || rawURL.contains("/home/")
            || rawURL.range(
                of: #"^[A-Za-z]:[\\/]"#,
                options: .regularExpression
            ) != nil
        {
            localLinkPages.append(index + 1)
        }
    }
}

if !blankPages.isEmpty {
    warnings.append("Metinsiz sayfalar: \(blankPages.map(String.init).joined(separator: ", "))")
}

for token in expectedChapterTokens where !fullText.localizedCaseInsensitiveContains(token) {
    failures.append("Bölüm başlığı PDF metninde bulunamadı: \(token)")
}

let chapterCardMarker = "30 saniyelik kart"
let chapterCardCount = fullText.components(separatedBy: chapterCardMarker).count - 1
let repositoryRoot = inputURL
    .deletingLastPathComponent()
    .deletingLastPathComponent()
let bookURL = repositoryRoot.appendingPathComponent("BOOK.md")
let expectedChapterCardCount: Int?
if let book = try? String(contentsOf: bookURL, encoding: .utf8) {
    expectedChapterCardCount =
        book.components(separatedBy: chapterCardMarker).count - 1
} else {
    expectedChapterCardCount = nil
    failures.append("BOOK.md okunamadı: \(bookURL.path)")
}
if let expectedChapterCardCount,
   chapterCardCount != expectedChapterCardCount
{
    failures.append(
        "Bölüm gövdesi bütünlüğü beklenen sayıda değil: "
            + "'\(chapterCardMarker)' \(chapterCardCount) kez bulundu, "
            + "\(expectedChapterCardCount) bekleniyordu"
    )
}

if fullText.contains("/Users/")
    || fullText.contains("/home/")
    || fullText.localizedCaseInsensitiveContains("mermaid-missing")
    || fullText.localizedCaseInsensitiveContains("mermaid-error")
{
    failures.append("PDF metninde yerel yol veya render durum izi bulundu")
}

if !localLinkPages.isEmpty {
    let pages = Array(Set(localLinkPages)).sorted().map(String.init).joined(separator: ", ")
    failures.append("Yerel dosya bağlantısı içeren sayfalar: \(pages)")
}

if internalLinkCount < 22 {
    failures.append(
        "PDF içinde en az 22 tıklanabilir bölüm bağlantısı bekleniyordu, "
            + "\(internalLinkCount) bulundu"
    )
}
if internalLinkTargetPages.count < 22 {
    failures.append(
        "PDF iç bağlantıları en az 22 farklı bölüm sayfasına gitmeliydi, "
            + "\(internalLinkTargetPages.count) farklı hedef bulundu"
    )
}

if document.outlineRoot == nil {
    failures.append("PDF outline/bookmark ağacı bulunamadı")
}

if !warnings.isEmpty {
    print("Uyarılar:")
    warnings.forEach { print("- \($0)") }
}

if !failures.isEmpty {
    FileHandle.standardError.write(Data("PDF denetimi başarısız:\n".utf8))
    failures.forEach {
        FileHandle.standardError.write(Data("- \($0)\n".utf8))
    }
    exit(1)
}

print(
    "PDF denetimi başarılı: \(document.pageCount) sayfa, 22 bölüm, "
        + "\(internalLinkCount) iç bağlantı, outline ve taşma/render kontrolü temiz."
)
