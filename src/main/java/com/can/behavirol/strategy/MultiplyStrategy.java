package com.can.behavirol.strategy;

public class MultiplyStrategy implements CalculationStrategy {

    @Override
    public int execute(int a, int b) {
        return a * b;
    }

    @Override
    public String name() {
        return "Çarpma";
    }
}
