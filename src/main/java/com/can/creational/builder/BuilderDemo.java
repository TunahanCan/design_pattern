package com.can.creational.builder;

import java.util.List;

public class BuilderDemo {

    public static void run() {
        System.out.println("3) Builder");

        System.out.println("Temel örnek — fluent builder:");
        Report custom = Report.builder("Aylık Operasyon Raporu")
                .summary("Operasyon ekibinin KPI sonuçları")
                .addSection("SLA")
                .addSection("Major Incidents")
                .addSection("Capacity Planning")
                .includeChart(true)
                .author("Can Demir")
                .build();
        System.out.println("Custom Builder       : " + custom.exportCard());

        System.out.println("Tekrarlanan kurulum — director reçeteleri:");
        ReportDirector director = new ReportDirector();
        Report quarterly = director.createQuarterlySalesReport();
        Report postmortem = director.createIncidentPostmortemReport("INC-2026-14");
        System.out.println("Director (Quarterly) : " + quarterly.exportCard());
        System.out.println("Director (Incident)  : " + postmortem.exportCard());

        System.out.println("Daha gerçekçi örnek — immutable rapordan yeni varyant:");
        Report executiveSummary = quarterly.toBuilder()
                .summary("Yönetim için tek sayfalık satış özeti")
                .sections(List.of("Özet", "Kritik Riskler"))
                .author("Executive Reporting Bot")
                .build();
        System.out.println("Derived Variant      : " + executiveSummary.exportCard());
        System.out.println();
    }
}
