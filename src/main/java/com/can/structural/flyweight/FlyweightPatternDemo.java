package com.can.structural.flyweight;

public class FlyweightPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("6) Flyweight");

        TreeFactory treeFactory = new TreeFactory();
        Forest forest = new Forest(treeFactory);

        for (int i = 0; i < 5; i++) {
            forest.plantTree(i, i + 1, "Çam", "Yeşil", "needle-texture");
            forest.plantTree(i + 10, i + 5, "Meşe", "Koyu Yeşil", "oak-texture");
            forest.plantTree(i + 20, i + 9, "Kiraz", "Pembe", "cherry-texture");
        }

        System.out.println("Toplam ağaç(context) sayısı: " + forest.getTreeCount());
        System.out.println("Paylaşılan TreeType(flyweight) sayısı: " + forest.getUniqueTreeTypeCount());
        System.out.println("Örnek çizim satırı: " + forest.drawAll().getFirst());
        System.out.println();
    }
}
