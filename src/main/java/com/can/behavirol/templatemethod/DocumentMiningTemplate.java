package com.can.behavirol.templatemethod;

public abstract class DocumentMiningTemplate {

    public final String process(String fileName) {
        openFile(fileName);
        String rawData = extractRawData(fileName);
        beforeAnalyze(rawData);
        String analyzedData = analyzeData(rawData);
        String report = createReport(analyzedData);
        afterReport(report);
        closeFile(fileName);
        return report;
    }

    protected abstract void openFile(String fileName);

    protected abstract String extractRawData(String fileName);

    protected String analyzeData(String rawData) {
        return "Standart analiz => " + rawData.toUpperCase();
    }

    protected String createReport(String analyzedData) {
        return "RAPOR: " + analyzedData;
    }

    protected void beforeAnalyze(String rawData) {
        // Hook method: varsayılan davranış boş bırakıldı.
    }

    protected void afterReport(String report) {
        // Hook method: varsayılan davranış boş bırakıldı.
    }

    protected abstract void closeFile(String fileName);
}
