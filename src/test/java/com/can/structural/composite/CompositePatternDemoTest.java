package com.can.structural.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompositePatternDemoTest {

    @Test
    void shouldCalculateNestedBoxTotalPrice() {
        Product keyboard = new Product("Keyboard", 1200);
        Product mouse = new Product("Mouse", 800);
        Product cable = new Product("Cable", 150);

        Box accessoryBox = new Box("Accessory Box", 40);
        accessoryBox.add(mouse);
        accessoryBox.add(cable);

        Box mainOrderBox = new Box("Main Box", 75);
        mainOrderBox.add(keyboard);
        mainOrderBox.add(accessoryBox);

        assertEquals(2265, mainOrderBox.getPrice());
    }

    @Test
    void shouldUpdateTotalWhenRemovingChild() {
        Box box = new Box("Box", 10);
        Product p1 = new Product("P1", 100);
        Product p2 = new Product("P2", 200);

        box.add(p1);
        box.add(p2);

        assertEquals(310, box.getPrice());

        box.remove(p1);

        assertEquals(210, box.getPrice());
    }
}
