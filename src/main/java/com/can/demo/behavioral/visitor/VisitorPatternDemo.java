package com.can.demo.behavioral.visitor;

import com.can.behavirol.visitor.City;
import com.can.behavirol.visitor.GeoNode;
import com.can.behavirol.visitor.GeoSummaryVisitor;
import com.can.behavirol.visitor.Industry;
import com.can.behavirol.visitor.RiskAuditVisitor;
import com.can.behavirol.visitor.SightSeeing;
import com.can.behavirol.visitor.XmlExportVisitor;

import java.util.List;

public final class VisitorPatternDemo {

    private VisitorPatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("10) Visitor");

        List<GeoNode> graph = List.of(
                new City("Istanbul", 15_800_000),
                new Industry("Marmara Chemical", "chemical"),
                new SightSeeing("Galata Tower", 1_200_000));

        XmlExportVisitor xmlExportVisitor = new XmlExportVisitor();
        RiskAuditVisitor riskAuditVisitor = new RiskAuditVisitor();
        GeoSummaryVisitor summaryVisitor = new GeoSummaryVisitor();

        for (GeoNode node : graph) {
            node.accept(xmlExportVisitor);
            node.accept(riskAuditVisitor);
            node.accept(summaryVisitor);
        }

        System.out.println("XML Export:");
        xmlExportVisitor.getXmlRows().forEach(System.out::println);

        System.out.println("\nRisk Audit:");
        riskAuditVisitor.getNotes().forEach(System.out::println);

        System.out.println("\nToplu Özet:");
        System.out.println(summaryVisitor.getSummary());
        System.out.println();
    }
}
