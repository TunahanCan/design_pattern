#!/usr/bin/env swift

import Foundation
import PDFKit

guard CommandLine.arguments.count == 4 else {
    FileHandle.standardError.write(
        Data(
            "Kullanım: add-pdf-outline.swift <input.pdf> <output.pdf> "
                .appending("<chapter-manifest.txt>\n")
                .utf8
        )
    )
    exit(2)
}

let inputURL = URL(fileURLWithPath: CommandLine.arguments[1])
let outputURL = URL(fileURLWithPath: CommandLine.arguments[2])
let manifestURL = URL(fileURLWithPath: CommandLine.arguments[3])
let repositoryRoot = manifestURL
    .deletingLastPathComponent()
    .deletingLastPathComponent()

guard let document = PDFDocument(url: inputURL) else {
    FileHandle.standardError.write(Data("PDF açılamadı: \(inputURL.path)\n".utf8))
    exit(3)
}

guard let manifest = try? String(contentsOf: manifestURL, encoding: .utf8) else {
    FileHandle.standardError.write(Data("Manifest okunamadı: \(manifestURL.path)\n".utf8))
    exit(4)
}

struct Chapter {
    let family: String
    let title: String
}

let chapters: [Chapter] = manifest
    .split(whereSeparator: \.isNewline)
    .compactMap { rawLine in
        let line = rawLine.trimmingCharacters(in: .whitespaces)
        guard !line.isEmpty, !line.hasPrefix("#") else {
            return nil
        }
        let columns = line.split(separator: "|", omittingEmptySubsequences: false)
        guard columns.count == 3 else {
            return nil
        }

        let chapterPath = String(columns[1])
        let chapterURL = repositoryRoot.appendingPathComponent(chapterPath)
        guard let markdown = try? String(contentsOf: chapterURL, encoding: .utf8),
              let titleLine = markdown
                .split(whereSeparator: \.isNewline)
                .first(where: { $0.hasPrefix("# ") })
        else {
            return nil
        }

        let family: String
        if chapterPath.contains("/creational/") {
            family = "Oluşturucu Desenler"
        } else if chapterPath.contains("/structural/") {
            family = "Yapısal Desenler"
        } else {
            family = "Davranışsal Desenler"
        }
        return Chapter(
            family: family,
            title: String(titleLine.dropFirst(2)).trimmingCharacters(in: .whitespaces)
        )
    }

guard chapters.count == 22 else {
    FileHandle.standardError.write(
        Data("Outline için 22 bölüm bekleniyordu, \(chapters.count) bulundu.\n".utf8)
    )
    exit(5)
}

func destination(for title: String) -> PDFDestination? {
    let searchTitle = title
        .components(separatedBy: " — ")
        .first?
        .components(separatedBy: " – ")
        .first?
        .trimmingCharacters(in: .whitespaces) ?? title
    let selections = document.findString(searchTitle, withOptions: [.caseInsensitive])
    var bestMatch: (page: PDFPage, pageIndex: Int, height: CGFloat)?
    for selection in selections {
        guard let page = selection.pages.first else {
            continue
        }
        let pageIndex = document.index(for: page)
        if pageIndex >= 10 {
            let height = selection.bounds(for: page).height
            if bestMatch == nil
                || height > bestMatch!.height
                || (height == bestMatch!.height && pageIndex < bestMatch!.pageIndex)
            {
                bestMatch = (page, pageIndex, height)
            }
        }
    }
    guard let match = bestMatch, match.height >= 20 else {
        return nil
    }
    let pageBounds = match.page.bounds(for: .mediaBox)
    return PDFDestination(
        page: match.page,
        at: CGPoint(x: pageBounds.minX, y: pageBounds.maxY)
    )
}

let root = PDFOutline()
root.label = "Java ile Tasarım Desenleri"
if let coverPage = document.page(at: 0) {
    let bounds = coverPage.bounds(for: .mediaBox)
    root.destination = PDFDestination(
        page: coverPage,
        at: CGPoint(x: bounds.minX, y: bounds.maxY)
    )
}

if let introPage = document.page(at: 1) {
    let intro = PDFOutline()
    intro.label = "Kitabı kullanma rehberi"
    let bounds = introPage.bounds(for: .mediaBox)
    intro.destination = PDFDestination(
        page: introPage,
        at: CGPoint(x: bounds.minX, y: bounds.maxY)
    )
    root.insertChild(intro, at: root.numberOfChildren)
}

var familyNodes: [String: PDFOutline] = [:]
for chapter in chapters {
    let familyNode: PDFOutline
    if let existing = familyNodes[chapter.family] {
        familyNode = existing
    } else {
        let created = PDFOutline()
        created.label = chapter.family
        familyNodes[chapter.family] = created
        root.insertChild(created, at: root.numberOfChildren)
        familyNode = created
    }

    guard let chapterDestination = destination(for: chapter.title) else {
        FileHandle.standardError.write(
            Data("PDF içinde bölüm başlığı bulunamadı: \(chapter.title)\n".utf8)
        )
        exit(6)
    }

    let chapterNode = PDFOutline()
    chapterNode.label = chapter.title
    chapterNode.destination = chapterDestination
    familyNode.insertChild(chapterNode, at: familyNode.numberOfChildren)
}

document.outlineRoot = root
guard document.write(to: outputURL) else {
    FileHandle.standardError.write(Data("Outline eklenmiş PDF yazılamadı.\n".utf8))
    exit(7)
}

print("PDF outline eklendi: 1 rehber, 3 aile, 22 bölüm.")
