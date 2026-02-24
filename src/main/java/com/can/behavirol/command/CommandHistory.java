package com.can.behavirol.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandHistory {

    private final Deque<Command> stack = new ArrayDeque<>();

    public void push(Command command) {
        stack.push(command);
    }

    public Command pop() {
        return stack.poll();
    }

    public int size() {
        return stack.size();
    }
}
