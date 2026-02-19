package com.can.behavirol.visitor;

import java.util.List;

public class VisitorPatternDemo {

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

        for (GeoNode node : graph) {
            node.accept(xmlExportVisitor);
            node.accept(riskAuditVisitor);
        }

        System.out.println("XML Export:");
        xmlExportVisitor.getXmlRows().forEach(System.out::println);

        System.out.println("\nRisk Audit:");
        riskAuditVisitor.getNotes().forEach(System.out::println);
        System.out.println();
    }
}
