package com.can.structural.adapter;

public class AdapterPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("1) Adapter");
        runShapeExample();
        runShippingIntegrationExample();
        System.out.println();
    }

    private static void runShapeExample() {
        System.out.println("Temel örnek — geometrik kontrat:");
        RoundHole hole = new RoundHole(5);
        RoundPeg roundPeg = new RoundPeg(5);

        System.out.println("Round peg (r=5) deliğe sığar mı? " + hole.fits(roundPeg));

        SquarePeg smallSquarePeg = new SquarePeg(5);
        SquarePeg largeSquarePeg = new SquarePeg(10);

        SquarePegAdapter smallAdapter = new SquarePegAdapter(smallSquarePeg);
        SquarePegAdapter largeAdapter = new SquarePegAdapter(largeSquarePeg);

        System.out.println("Square peg (w=5) adapter ile sığar mı? " + hole.fits(smallAdapter));
        System.out.println("Square peg (w=10) adapter ile sığar mı? " + hole.fits(largeAdapter));
    }

    private static void runShippingIntegrationExample() {
        System.out.println("Gerçekçi örnek — eski kargo API entegrasyonu:");
        ShippingService shippingService = new LegacyCargoAdapter(new LegacyCargoApi());
        DeliveryQuote quote = shippingService.quote(new Parcel("06000", 2_500));

        System.out.printf(
            "%s -> %s TL, tahmini %d gün%n",
            quote.provider(),
            quote.priceTry(),
            quote.estimatedDays()
        );
    }
}
