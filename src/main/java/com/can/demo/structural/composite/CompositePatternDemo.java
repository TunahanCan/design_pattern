package com.can.demo.structural.composite;

import com.can.structural.composite.Box;
import com.can.structural.composite.Product;
import java.math.BigDecimal;

/**
 * Executable composition root for the Composite example.
 *
 * <p>The reusable tree model remains in {@code com.can.structural.composite};
 * this class only builds one illustrative object graph.</p>
 */
public final class CompositePatternDemo {

    private CompositePatternDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("3) Composite");

        Product keyboard = new Product("Klavye", new BigDecimal("1200.00"));
        Product mouse = new Product("Mouse", new BigDecimal("800.00"));
        Product cable = new Product("Kablo", new BigDecimal("150.00"));

        Box accessoryBox = new Box("Aksesuar Kutusu", new BigDecimal("40.00"));
        accessoryBox.add(mouse);
        accessoryBox.add(cable);

        Box mainOrderBox = new Box("Ana Sipariş Kutusu", new BigDecimal("75.00"));
        mainOrderBox.add(keyboard);
        mainOrderBox.add(accessoryBox);

        System.out.println(
            mainOrderBox.getName()
                + " toplam fiyat: "
                + mainOrderBox.getPriceAmount()
                + " TL"
        );
        System.out.println();
    }
}
