package com.can;

import com.can.behavirol.chainofresponsibility.ChainOfResponsibilityDemo;
import com.can.behavirol.command.CommandPatternDemo;
import com.can.behavirol.iterator.IteratorPatternDemo;
import com.can.behavirol.mediator.MediatorPatternDemo;
import com.can.behavirol.memento.MementoPatternDemo;
import com.can.behavirol.observer.ObserverPatternDemo;
import com.can.behavirol.state.StatePatternDemo;
import com.can.behavirol.strategy.StrategyPatternDemo;
import com.can.behavirol.templatemethod.TemplateMethodPatternDemo;
import com.can.behavirol.visitor.VisitorPatternDemo;
import com.can.creational.abstractfactory.AbstractFactoryDemo;
import com.can.creational.builder.BuilderDemo;
import com.can.creational.factorymethod.FactoryMethodDemo;
import com.can.creational.prototype.PrototypeDemo;
import com.can.creational.singleton.SingletonDemo;
import com.can.structural.adapter.AdapterPatternDemo;
import com.can.structural.bridge.BridgePatternDemo;
import com.can.structural.composite.CompositePatternDemo;

public class Main
{
    public static void main(String[] args) {
        System.out.println("=== CREATIONAL DESIGN PATTERNS ===\n");

        FactoryMethodDemo.run();
        AbstractFactoryDemo.run();
        BuilderDemo.run();
        PrototypeDemo.run();
        SingletonDemo.run();

        System.out.println("=== STRUCTURAL DESIGN PATTERNS ===\n");
        AdapterPatternDemo.run();
        BridgePatternDemo.run();
        CompositePatternDemo.run();

        System.out.println("=== BEHAVIORAL DESIGN PATTERNS ===\n");
        ChainOfResponsibilityDemo.run();
        CommandPatternDemo.run();
        IteratorPatternDemo.run();
        MediatorPatternDemo.run();
        MementoPatternDemo.run();
        ObserverPatternDemo.run();
        StatePatternDemo.run();
        StrategyPatternDemo.run();
        TemplateMethodPatternDemo.run();
        VisitorPatternDemo.run();
    }
}
