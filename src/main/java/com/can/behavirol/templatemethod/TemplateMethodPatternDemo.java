package com.can.behavirol.templatemethod;

public class TemplateMethodPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("9) Template Method");

        DocumentMiningTemplate pdfMiner = new PdfDocumentMiner();
        String pdfReport = pdfMiner.process("kurumsal-fatura.pdf");
        System.out.println(pdfReport);

        System.out.println();

        DocumentMiningTemplate csvMiner = new CsvDocumentMiner();
        String csvReport = csvMiner.process("satis-raporu.csv");
        System.out.println(csvReport);
        System.out.println();
    }
}
