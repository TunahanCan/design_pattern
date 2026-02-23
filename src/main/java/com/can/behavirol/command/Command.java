package com.can.behavirol.command;

public interface Command
{
    boolean execute();

    void undo();

    String name();
}
