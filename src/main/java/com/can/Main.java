package com.can;

import com.can.behavirol.chainofresponsibility.ChainOfResponsibilityDemo;
import com.can.behavirol.command.CommandPatternDemo;
import com.can.behavirol.iterator.IteratorPatternDemo;
import com.can.creational.abstractfactory.AbstractFactoryDemo;
import com.can.creational.builder.BuilderDemo;
import com.can.creational.factorymethod.FactoryMethodDemo;
import com.can.creational.prototype.PrototypeDemo;
import com.can.creational.singleton.SingletonDemo;

public class Main
{
    public static void main(String[] args) {
        System.out.println("=== CREATIONAL DESIGN PATTERNS ===\n");

        FactoryMethodDemo.run();
        AbstractFactoryDemo.run();
        BuilderDemo.run();
        PrototypeDemo.run();
        SingletonDemo.run();

        System.out.println("=== BEHAVIORAL DESIGN PATTERNS ===\n");
        ChainOfResponsibilityDemo.run();
        CommandPatternDemo.run();
        IteratorPatternDemo.run();
    }
}
