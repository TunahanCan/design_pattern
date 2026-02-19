package com.can.behavirol.strategy;

public class AddStrategy implements CalculationStrategy {

    @Override
    public int execute(int a, int b) {
        return a + b;
    }

    @Override
    public String name() {
        return "Toplama";
    }
}
