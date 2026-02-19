package com.can.behavirol.strategy;

public class CalculatorContext {

    private CalculationStrategy strategy;

    public CalculatorContext(CalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(CalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public int calculate(int a, int b) {
        return strategy.execute(a, b);
    }

    public String getStrategyName() {
        return strategy.name();
    }
}
