package com.can.behavirol.templatemethod;

public class CsvDocumentMiner extends DocumentMiningTemplate {

    @Override
    protected void openFile(String fileName) {
        System.out.println("CSV dosyası açılıyor: " + fileName);
    }

    @Override
    protected String extractRawData(String fileName) {
        return "csv: urun, adet, birim_fiyat";
    }

    @Override
    protected String analyzeData(String rawData) {
        return "CSV özel analizi => satır bazlı normalizasyon: " + rawData;
    }

    @Override
    protected String createReport(String analyzedData) {
        return "CSV RAPORU: " + analyzedData;
    }

    @Override
    protected void afterReport(String report) {
        System.out.println("CSV raporu veri ambarına yazıldı.");
    }

    @Override
    protected void closeFile(String fileName) {
        System.out.println("CSV dosyası kapatılıyor: " + fileName);
    }
}
