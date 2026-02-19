package com.can.behavirol.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class VisitorPatternDemoTest {

    @Test
    void shouldExportNodesAndCreateRiskAuditWithoutChangingElements() {
        List<GeoNode> graph = List.of(
                new City("Ankara", 5_800_000),
                new Industry("Aegean Textiles", "textile"),
                new SightSeeing("Anitkabir", 900_000));

        XmlExportVisitor xmlExportVisitor = new XmlExportVisitor();
        RiskAuditVisitor riskAuditVisitor = new RiskAuditVisitor();

        for (GeoNode node : graph) {
            node.accept(xmlExportVisitor);
            node.accept(riskAuditVisitor);
        }

        assertEquals(3, xmlExportVisitor.getXmlRows().size());
        assertEquals("<city name=\"Ankara\" population=\"5800000\" />", xmlExportVisitor.getXmlRows().getFirst());

        assertEquals(3, riskAuditVisitor.getNotes().size());
        assertEquals("Ankara: Yüksek yoğunluk - deprem/tahliye planı kritik.", riskAuditVisitor.getNotes().getFirst());
    }
}
