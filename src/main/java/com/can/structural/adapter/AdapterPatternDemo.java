package com.can.structural.adapter;

public class AdapterPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("1) Adapter");

        RoundHole hole = new RoundHole(5);
        RoundPeg roundPeg = new RoundPeg(5);

        System.out.println("Round peg (r=5) deliğe sığar mı? " + hole.fits(roundPeg));

        SquarePeg smallSquarePeg = new SquarePeg(5);
        SquarePeg largeSquarePeg = new SquarePeg(10);

        SquarePegAdapter smallAdapter = new SquarePegAdapter(smallSquarePeg);
        SquarePegAdapter largeAdapter = new SquarePegAdapter(largeSquarePeg);

        System.out.println("Square peg (w=5) adapter ile sığar mı? " + hole.fits(smallAdapter));
        System.out.println("Square peg (w=10) adapter ile sığar mı? " + hole.fits(largeAdapter));
        System.out.println();
    }
}
