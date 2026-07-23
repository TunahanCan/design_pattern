#!/usr/bin/env swift

import CoreGraphics
import Foundation
import ImageIO
import UniformTypeIdentifiers

guard CommandLine.arguments.count >= 3 else {
    FileHandle.standardError.write(
        Data("Kullanım: render-pdf-pages.swift <input.pdf> <output-dir> [scale]\n".utf8)
    )
    exit(2)
}

let inputPath = CommandLine.arguments[1]
let outputPath = CommandLine.arguments[2]
let scale = CommandLine.arguments.count >= 4
    ? max(1.0, Double(CommandLine.arguments[3]) ?? 2.0)
    : 2.0

let inputURL = URL(fileURLWithPath: inputPath)
let outputURL = URL(fileURLWithPath: outputPath, isDirectory: true)

guard let document = CGPDFDocument(inputURL as CFURL) else {
    FileHandle.standardError.write(Data("PDF açılamadı: \(inputPath)\n".utf8))
    exit(3)
}

try FileManager.default.createDirectory(
    at: outputURL,
    withIntermediateDirectories: true
)

let colorSpace = CGColorSpaceCreateDeviceRGB()
let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue

for pageNumber in 1...document.numberOfPages {
    autoreleasepool {
        guard let page = document.page(at: pageNumber) else {
            FileHandle.standardError.write(Data("Sayfa okunamadı: \(pageNumber)\n".utf8))
            exit(4)
        }

        let box = page.getBoxRect(.mediaBox)
        let width = max(1, Int((box.width * scale).rounded(.up)))
        let height = max(1, Int((box.height * scale).rounded(.up)))

        guard let context = CGContext(
            data: nil,
            width: width,
            height: height,
            bitsPerComponent: 8,
            bytesPerRow: 0,
            space: colorSpace,
            bitmapInfo: bitmapInfo
        ) else {
            FileHandle.standardError.write(Data("Bitmap context oluşturulamadı.\n".utf8))
            exit(5)
        }

        context.setFillColor(CGColor(gray: 1.0, alpha: 1.0))
        context.fill(CGRect(x: 0, y: 0, width: width, height: height))

        let destinationRect = CGRect(x: 0, y: 0, width: width, height: height)
        let transform = page.getDrawingTransform(
            .mediaBox,
            rect: destinationRect,
            rotate: 0,
            preserveAspectRatio: true
        )
        context.concatenate(transform)
        context.interpolationQuality = .high
        context.drawPDFPage(page)

        guard let image = context.makeImage() else {
            FileHandle.standardError.write(Data("Sayfa görseli üretilemedi: \(pageNumber)\n".utf8))
            exit(6)
        }

        let filename = String(format: "page-%03d.png", pageNumber)
        let destinationURL = outputURL.appendingPathComponent(filename)
        guard let destination = CGImageDestinationCreateWithURL(
            destinationURL as CFURL,
            UTType.png.identifier as CFString,
            1,
            nil
        ) else {
            FileHandle.standardError.write(Data("PNG hedefi oluşturulamadı: \(filename)\n".utf8))
            exit(7)
        }

        CGImageDestinationAddImage(destination, image, nil)
        guard CGImageDestinationFinalize(destination) else {
            FileHandle.standardError.write(Data("PNG yazılamadı: \(filename)\n".utf8))
            exit(8)
        }
    }
}

print("Render tamamlandı: \(document.numberOfPages) sayfa @ \(scale)x")
