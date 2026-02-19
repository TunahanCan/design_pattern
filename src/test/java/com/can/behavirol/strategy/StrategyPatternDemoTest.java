package com.can.behavirol.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StrategyPatternDemoTest {

    @Test
    void shouldSwitchStrategiesAtRuntime() {
        CalculatorContext context = new CalculatorContext(new AddStrategy());

        assertEquals(16, context.calculate(12, 4));
        assertEquals("Toplama", context.getStrategyName());

        context.setStrategy(new SubtractStrategy());
        assertEquals(8, context.calculate(12, 4));
        assertEquals("Çıkarma", context.getStrategyName());

        context.setStrategy(new MultiplyStrategy());
        assertEquals(48, context.calculate(12, 4));
        assertEquals("Çarpma", context.getStrategyName());
    }
}
