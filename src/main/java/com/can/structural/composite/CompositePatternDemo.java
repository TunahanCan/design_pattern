package com.can.structural.composite;

public class CompositePatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("3) Composite");

        Product keyboard = new Product("Klavye", 1200);
        Product mouse = new Product("Mouse", 800);
        Product cable = new Product("Kablo", 150);

        Box accessoryBox = new Box("Aksesuar Kutusu", 40);
        accessoryBox.add(mouse);
        accessoryBox.add(cable);

        Box mainOrderBox = new Box("Ana Sipariş Kutusu", 75);
        mainOrderBox.add(keyboard);
        mainOrderBox.add(accessoryBox);

        System.out.println(mainOrderBox.getName() + " toplam fiyat: " + mainOrderBox.getPrice() + " TL");
        System.out.println();
    }
}
