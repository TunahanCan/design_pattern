package com.can.behavirol.strategy;

public class StrategyPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("8) Strategy");

        int first = 12;
        int second = 4;

        CalculatorContext calculator = new CalculatorContext(new AddStrategy());
        System.out.println(calculator.getStrategyName() + ": " + first + " ve " + second + " => " + calculator.calculate(first, second));

        calculator.setStrategy(new SubtractStrategy());
        System.out.println(calculator.getStrategyName() + ": " + first + " ve " + second + " => " + calculator.calculate(first, second));

        calculator.setStrategy(new MultiplyStrategy());
        System.out.println(calculator.getStrategyName() + ": " + first + " ve " + second + " => " + calculator.calculate(first, second));
        System.out.println();
    }
}
