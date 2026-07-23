package com.can.creational.builder;

import java.util.List;
import java.util.Objects;

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
        String normalizedIncidentId = Objects.requireNonNull(
                incidentId,
                "incidentId cannot be null"
        ).trim();
        if (normalizedIncidentId.isBlank()) {
            throw new IllegalArgumentException("incidentId cannot be blank");
        }

        return Report.builder("Incident Postmortem - " + normalizedIncidentId)
                .summary("Olayın kök neden analizi ve iyileştirme aksiyonları")
                .addSection("Timeline")
                .addSection("Root Cause")
                .addSection("Action Items")
                .includeChart(false)
                .author("SRE Team")
                .build();
    }
}
