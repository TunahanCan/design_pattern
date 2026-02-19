package com.can.structural.flyweight;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class FlyweightPatternDemoTest {

    @Test
    void shouldReuseTreeTypeInstances() {
        TreeFactory factory = new TreeFactory();

        TreeType pineA = factory.getTreeType("Çam", "Yeşil", "needle-texture");
        TreeType pineB = factory.getTreeType("Çam", "Yeşil", "needle-texture");
        TreeType oak = factory.getTreeType("Meşe", "Koyu Yeşil", "oak-texture");

        assertSame(pineA, pineB);
        assertEquals(2, factory.getTreeTypeCount());
        assertNotNull(oak);
    }

    @Test
    void shouldKeepManyContextsWithFewFlyweights() {
        TreeFactory factory = new TreeFactory();
        Forest forest = new Forest(factory);

        for (int i = 0; i < 100; i++) {
            forest.plantTree(i, i, "Çam", "Yeşil", "needle-texture");
            forest.plantTree(i, i + 1, "Meşe", "Koyu Yeşil", "oak-texture");
        }

        assertEquals(200, forest.getTreeCount());
        assertEquals(2, forest.getUniqueTreeTypeCount());
    }
}
