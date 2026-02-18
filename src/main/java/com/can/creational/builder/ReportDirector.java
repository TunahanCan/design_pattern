package com.can.creational.builder;

import java.util.List;

public class ReportDirector {

    public Report createQuarterlySalesReport() {
        return Report.builder("Q1 Satış Raporu")
                .summary("İlk çeyrek satış performansı")
                .sections(List.of("Özet", "Bölgesel Dağılım", "Riskler"))
                .includeChart(true)
                .author("Sales Analytics Bot")
                .build();
    }

    public Report createIncidentPostmortemReport(String incidentId) {
        return Report.builder("Incident Postmortem - " + incidentId)
                .summary("Olayın kök neden analizi ve iyileştirme aksiyonları")
                .addSection("Timeline")
                .addSection("Root Cause")
                .addSection("Action Items")
                .includeChart(false)
                .author("SRE Team")
                .build();
    }
}
