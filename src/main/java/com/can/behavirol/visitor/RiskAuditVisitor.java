package com.can.behavirol.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskAuditVisitor implements GeoNodeVisitor {

    private final List<String> notes = new ArrayList<>();

    @Override
    public void visitCity(City city) {
        if (city.getPopulation() > 1_000_000) {
            notes.add(city.getName() + ": Yüksek yoğunluk - deprem/tahliye planı kritik.");
            return;
        }

        notes.add(city.getName() + ": Orta yoğunluk - standart afet planı yeterli.");
    }

    @Override
    public void visitIndustry(Industry industry) {
        if ("chemical".equalsIgnoreCase(industry.getSector())) {
            notes.add(industry.getName() + ": Kimya sektörü - ek güvenlik denetimi gerekli.");
            return;
        }

        notes.add(industry.getName() + ": " + industry.getSector() + " sektörü - rutin denetim.");
    }

    @Override
    public void visitSightSeeing(SightSeeing sightSeeing) {
        if (sightSeeing.getAnnualVisitors() > 500_000) {
            notes.add(sightSeeing.getName() + ": Yüksek ziyaretçi trafiği - crowd control artırılmalı.");
            return;
        }

        notes.add(sightSeeing.getName() + ": Normal ziyaretçi trafiği.");
    }

    public List<String> getNotes() {
        return Collections.unmodifiableList(notes);
    }
}
