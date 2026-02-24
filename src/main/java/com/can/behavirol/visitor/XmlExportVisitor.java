package com.can.behavirol.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlExportVisitor implements GeoNodeVisitor
{

    private final List<String> xmlRows = new ArrayList<>();

    @Override
    public void visitCity(City city) {
        xmlRows.add("<city name=\"" + city.getName() + "\" population=\"" + city.getPopulation() + "\" />");
    }

    @Override
    public void visitIndustry(Industry industry) {
        xmlRows.add("<industry name=\"" + industry.getName() + "\" sector=\"" + industry.getSector() + "\" />");
    }

    @Override
    public void visitSightSeeing(SightSeeing sightSeeing) {
        xmlRows.add(
                "<sightseeing name=\""
                        + sightSeeing.getName()
                        + "\" annualVisitors=\""
                        + sightSeeing.getAnnualVisitors()
                        + "\" />");
    }
    public List<String> getXmlRows() {
        return Collections.unmodifiableList(xmlRows);
    }
}
