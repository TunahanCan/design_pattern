package com.can.behavirol.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Birden fazla komutu araç çubuğuna tek bir kullanıcı niyeti olarak bağlar.
 */
public class MacroCommand implements Command {

    private final String name;
    private final List<Command> commands;
    private final Deque<List<Command>> executionHistory = new ArrayDeque<>();

    public MacroCommand(String name, List<Command> commands) {
        this.name = name;
        this.commands = List.copyOf(commands);
    }

    @Override
    public boolean execute() {
        List<Command> executedCommands = new ArrayList<>();
        try {
            for (Command command : commands) {
                if (command.execute()) {
                    executedCommands.add(command);
                }
            }
        } catch (RuntimeException error) {
            undoInReverseOrder(executedCommands, error);
            throw error;
        }

        if (executedCommands.isEmpty()) {
            return false;
        }

        executionHistory.push(List.copyOf(executedCommands));
        return true;
    }

    @Override
    public void undo() {
        List<Command> latestExecution = executionHistory.poll();
        if (latestExecution != null) {
            RuntimeException undoFailure = undoInReverseOrder(latestExecution, null);
            if (undoFailure != null) {
                throw undoFailure;
            }
        }
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * Bütün undo adımlarını ters sırada dener.
     *
     * <p>Bir primary hata verilmişse undo hataları ona suppressed olarak eklenir.
     * Normal undo akışında ise ilk undo hatası primary olur; sonraki undo hataları
     * deterministik çağrı sırasıyla ona suppressed olarak eklenir.</p>
     */
    private static RuntimeException undoInReverseOrder(List<Command> commands,
                                                       RuntimeException primaryFailure) {
        RuntimeException failure = primaryFailure;
        for (int index = commands.size() - 1; index >= 0; index--) {
            try {
                commands.get(index).undo();
            } catch (RuntimeException undoFailure) {
                if (failure == null) {
                    failure = undoFailure;
                } else if (failure != undoFailure) {
                    failure.addSuppressed(undoFailure);
                }
            }
        }
        return failure;
    }
}
