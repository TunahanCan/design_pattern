package com.can.behavirol.templatemethod;

public class PdfDocumentMiner extends DocumentMiningTemplate {

    @Override
    protected void openFile(String fileName) {
        System.out.println("PDF dosyası açılıyor: " + fileName);
    }

    @Override
    protected String extractRawData(String fileName) {
        return "pdf: fatura kalemleri, toplam tutar, vergi";
    }

    @Override
    protected void beforeAnalyze(String rawData) {
        System.out.println("PDF OCR kalitesi kontrol ediliyor...");
    }

    @Override
    protected void closeFile(String fileName) {
        System.out.println("PDF dosyası kapatılıyor: " + fileName);
    }
}
