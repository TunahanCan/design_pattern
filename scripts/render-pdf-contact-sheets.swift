#!/usr/bin/env swift

import CoreGraphics
import CoreText
import Foundation
import ImageIO
import UniformTypeIdentifiers

guard CommandLine.arguments.count >= 3 else {
    FileHandle.standardError.write(
        Data("Kullanım: render-pdf-contact-sheets.swift <input.pdf> <output-dir> [scale]\n".utf8)
    )
    exit(2)
}

let inputURL = URL(fileURLWithPath: CommandLine.arguments[1])
let outputURL = URL(fileURLWithPath: CommandLine.arguments[2], isDirectory: true)
let scale = CommandLine.arguments.count >= 4
    ? max(0.75, Double(CommandLine.arguments[3]) ?? 1.0)
    : 1.0

guard let document = CGPDFDocument(inputURL as CFURL),
      let firstPage = document.page(at: 1)
else {
    FileHandle.standardError.write(Data("PDF açılamadı.\n".utf8))
    exit(3)
}

try FileManager.default.createDirectory(
    at: outputURL,
    withIntermediateDirectories: true
)

let columns = 3
let rows = 3
let pagesPerSheet = columns * rows
let pageBox = firstPage.getBoxRect(.mediaBox)
let pageWidth = Int((pageBox.width * scale).rounded(.up))
let pageHeight = Int((pageBox.height * scale).rounded(.up))
let gutter = max(14, Int(16 * scale))
let labelHeight = max(24, Int(28 * scale))
let sheetWidth = columns * pageWidth + (columns + 1) * gutter
let sheetHeight = rows * (pageHeight + labelHeight) + (rows + 1) * gutter
let sheetCount = Int(ceil(Double(document.numberOfPages) / Double(pagesPerSheet)))
let colorSpace = CGColorSpaceCreateDeviceRGB()
let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue

func drawLabel(_ text: String, in context: CGContext, rect: CGRect) {
    let attributes: [NSAttributedString.Key: Any] = [
        NSAttributedString.Key(kCTFontAttributeName as String):
            CTFontCreateWithName("AvenirNext-DemiBold" as CFString, 12 * scale, nil),
        NSAttributedString.Key(kCTForegroundColorAttributeName as String):
            CGColor(red: 0.08, green: 0.14, blue: 0.24, alpha: 1.0),
    ]
    let attributed = NSAttributedString(string: text, attributes: attributes)
    let line = CTLineCreateWithAttributedString(attributed)
    context.textPosition = CGPoint(x: rect.minX, y: rect.minY + 6 * scale)
    CTLineDraw(line, context)
}

for sheetIndex in 0..<sheetCount {
    autoreleasepool {
        guard let context = CGContext(
            data: nil,
            width: sheetWidth,
            height: sheetHeight,
            bitsPerComponent: 8,
            bytesPerRow: 0,
            space: colorSpace,
            bitmapInfo: bitmapInfo
        ) else {
            FileHandle.standardError.write(Data("Contact sheet context oluşturulamadı.\n".utf8))
            exit(4)
        }

        context.setFillColor(CGColor(red: 0.90, green: 0.92, blue: 0.95, alpha: 1.0))
        context.fill(CGRect(x: 0, y: 0, width: sheetWidth, height: sheetHeight))

        for slot in 0..<pagesPerSheet {
            let pageNumber = sheetIndex * pagesPerSheet + slot + 1
            guard pageNumber <= document.numberOfPages,
                  let page = document.page(at: pageNumber)
            else {
                continue
            }

            let column = slot % columns
            let rowFromTop = slot / columns
            let x = gutter + column * (pageWidth + gutter)
            let y = sheetHeight
                - gutter
                - (rowFromTop + 1) * (pageHeight + labelHeight)

            let pageRect = CGRect(x: x, y: y + labelHeight, width: pageWidth, height: pageHeight)
            context.saveGState()
            context.setShadow(
                offset: CGSize(width: 0, height: -2 * scale),
                blur: 5 * scale,
                color: CGColor(gray: 0.1, alpha: 0.22)
            )
            context.setFillColor(CGColor(gray: 1.0, alpha: 1.0))
            context.fill(pageRect)
            context.restoreGState()

            context.saveGState()
            let transform = page.getDrawingTransform(
                .mediaBox,
                rect: pageRect,
                rotate: 0,
                preserveAspectRatio: true
            )
            context.concatenate(transform)
            context.interpolationQuality = .high
            context.drawPDFPage(page)
            context.restoreGState()

            drawLabel(
                "Sayfa \(pageNumber)",
                in: context,
                rect: CGRect(x: x, y: y, width: pageWidth, height: labelHeight)
            )
        }

        guard let image = context.makeImage() else {
            FileHandle.standardError.write(Data("Contact sheet görseli üretilemedi.\n".utf8))
            exit(5)
        }

        let first = sheetIndex * pagesPerSheet + 1
        let last = min(document.numberOfPages, first + pagesPerSheet - 1)
        let filename = String(
            format: "sheet-%03d-pages-%03d-%03d.png",
            sheetIndex + 1,
            first,
            last
        )
        let destinationURL = outputURL.appendingPathComponent(filename)
        guard let destination = CGImageDestinationCreateWithURL(
            destinationURL as CFURL,
            UTType.png.identifier as CFString,
            1,
            nil
        ) else {
            FileHandle.standardError.write(Data("Contact sheet hedefi oluşturulamadı.\n".utf8))
            exit(6)
        }

        CGImageDestinationAddImage(destination, image, nil)
        guard CGImageDestinationFinalize(destination) else {
            FileHandle.standardError.write(Data("Contact sheet yazılamadı: \(filename)\n".utf8))
            exit(7)
        }
    }
}

print(
    "Contact sheet tamamlandı: \(document.numberOfPages) sayfa, \(sheetCount) görsel, \(scale)x"
)
