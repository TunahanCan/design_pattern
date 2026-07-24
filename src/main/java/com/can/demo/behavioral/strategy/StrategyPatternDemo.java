package com.can.demo.behavioral.strategy;

import com.can.behavirol.strategy.AddStrategy;
import com.can.behavirol.strategy.CalculatorContext;
import com.can.behavirol.strategy.DeliveryPlanner;
import com.can.behavirol.strategy.ExpressDeliveryStrategy;
import com.can.behavirol.strategy.MultiplyStrategy;
import com.can.behavirol.strategy.Shipment;
import com.can.behavirol.strategy.StandardDeliveryStrategy;
import com.can.behavirol.strategy.SubtractStrategy;

public final class StrategyPatternDemo {

    private StrategyPatternDemo() {
    }

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

        Shipment shipment = new Shipment(3, true, false);
        DeliveryPlanner deliveryPlanner = new DeliveryPlanner(new StandardDeliveryStrategy());
        System.out.println("Standart teklif: " + deliveryPlanner.quote(shipment));
        deliveryPlanner.setStrategy(new ExpressDeliveryStrategy());
        System.out.println("Ekspres teklif: " + deliveryPlanner.quote(shipment));
        System.out.println();
    }
}
