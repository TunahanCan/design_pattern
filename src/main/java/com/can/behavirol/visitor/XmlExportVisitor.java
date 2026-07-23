package com.can.behavirol.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlExportVisitor implements GeoNodeVisitor
{

    private final List<String> xmlRows = new ArrayList<>();

    @Override
    public void visitCity(City city) {
        xmlRows.add("<city name=\"" + escape(city.getName()) + "\" population=\"" + city.getPopulation() + "\" />");
    }

    @Override
    public void visitIndustry(Industry industry) {
        xmlRows.add("<industry name=\"" + escape(industry.getName()) + "\" sector=\"" + escape(industry.getSector()) + "\" />");
    }

    @Override
    public void visitSightSeeing(SightSeeing sightSeeing) {
        xmlRows.add(
                "<sightseeing name=\""
                        + escape(sightSeeing.getName())
                        + "\" annualVisitors=\""
                        + sightSeeing.getAnnualVisitors()
                        + "\" />");
    }
    public List<String> getXmlRows() {
        return Collections.unmodifiableList(xmlRows);
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;");
    }
}
