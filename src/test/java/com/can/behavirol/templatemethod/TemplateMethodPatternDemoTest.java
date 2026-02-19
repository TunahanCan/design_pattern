package com.can.behavirol.templatemethod;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TemplateMethodPatternDemoTest {

    @Test
    void shouldProduceFormatSpecificReportsWithSameTemplateFlow() {
        DocumentMiningTemplate pdfMiner = new PdfDocumentMiner();
        String pdfReport = pdfMiner.process("kurumsal-fatura.pdf");
        assertTrue(pdfReport.startsWith("RAPOR:"));
        assertTrue(pdfReport.contains("PDF: FATURA KALEMLERI"));

        DocumentMiningTemplate csvMiner = new CsvDocumentMiner();
        String csvReport = csvMiner.process("satis-raporu.csv");
        assertTrue(csvReport.startsWith("CSV RAPORU:"));
        assertTrue(csvReport.contains("CSV özel analizi"));
    }
}
