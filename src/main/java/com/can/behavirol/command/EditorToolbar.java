package com.can.behavirol.command;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditorToolbar {
    private final Map<String, Command> buttons = new LinkedHashMap<>();
    private final CommandHistory history;

    public EditorToolbar(CommandHistory history) {
        this.history = history;
    }

    public void setButton(String buttonName, Command command) {
        buttons.put(buttonName, command);
    }

    public void click(String buttonName) {
        Command command = buttons.get(buttonName);
        if (command == null) {
            throw new IllegalArgumentException("Tanımsız buton: " + buttonName);
        }

        boolean shouldBeTracked = command.execute();
        if (shouldBeTracked) {
            history.push(command);
        }
    }

    public void undoLast() {
        Command command = history.pop();
        if (command != null) {
            command.undo();
        }
    }
}
