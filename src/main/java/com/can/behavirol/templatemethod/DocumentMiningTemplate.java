package com.can.behavirol.templatemethod;

import java.util.Locale;

public abstract class DocumentMiningTemplate {

    public final String process(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName cannot be blank");
        }

        openFile(fileName);
        Throwable primaryFailure = null;
        try {
            String rawData = extractRawData(fileName);
            beforeAnalyze(rawData);
            String analyzedData = analyzeData(rawData);
            String report = createReport(analyzedData);
            afterReport(report);
            return report;
        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                closeFile(fileName);
            } catch (RuntimeException | Error closeFailure) {
                if (primaryFailure == null) {
                    throw closeFailure;
                }
                if (primaryFailure != closeFailure) {
                    primaryFailure.addSuppressed(closeFailure);
                }
            }
        }
    }

    protected abstract void openFile(String fileName);

    protected abstract String extractRawData(String fileName);

    protected String analyzeData(String rawData) {
        return "Standart analiz => " + rawData.toUpperCase(Locale.ROOT);
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
