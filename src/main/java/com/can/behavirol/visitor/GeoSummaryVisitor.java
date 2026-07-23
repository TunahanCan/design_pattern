package com.can.behavirol.visitor;

/**
 * Element sınıflarını değiştirmeden heterojen grafikten sayısal özet üretir.
 */
public class GeoSummaryVisitor implements GeoNodeVisitor {

    private int cityCount;
    private long totalPopulation;
    private int industryCount;
    private int sightSeeingCount;
    private long totalAnnualVisitors;

    @Override
    public void visitCity(City city) {
        cityCount++;
        totalPopulation += city.getPopulation();
    }

    @Override
    public void visitIndustry(Industry industry) {
        industryCount++;
    }

    @Override
    public void visitSightSeeing(SightSeeing sightSeeing) {
        sightSeeingCount++;
        totalAnnualVisitors += sightSeeing.getAnnualVisitors();
    }

    public GeoSummary getSummary() {
        return new GeoSummary(
                cityCount,
                totalPopulation,
                industryCount,
                sightSeeingCount,
                totalAnnualVisitors
        );
    }
}
