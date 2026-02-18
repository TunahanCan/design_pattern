package com.can.creational.builder;

public class BuilderDemo {

    public static void run() {
        System.out.println("3) Builder");

        ReportDirector director = new ReportDirector();
        Report quarterly = director.createQuarterlySalesReport();

        Report custom = Report.builder("Aylık Operasyon Raporu")
                .summary("Operasyon ekibinin KPI sonuçları")
                .addSection("SLA")
                .addSection("Major Incidents")
                .addSection("Capacity Planning")
                .includeChart(true)
                .author("Can Demir")
                .build();

        Report postmortem = director.createIncidentPostmortemReport("INC-2026-14");

        System.out.println("Director (Quarterly) : " + quarterly.exportCard());
        System.out.println("Custom Builder       : " + custom.exportCard());
        System.out.println("Director (Incident)  : " + postmortem.exportCard());
        System.out.println();
    }
}
