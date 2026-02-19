package com.can.behavirol.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class EditorHistory {

    private final Deque<TextEditor.Snapshot> stack = new ArrayDeque<>();

    public void push(TextEditor.EditorMemento memento) {
        if (memento instanceof TextEditor.Snapshot snapshot) {
            stack.push(snapshot);
        }
    }

    public Optional<TextEditor.Snapshot> popLast() {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(stack.pop());
    }

    public void printTimeline() {
        if (stack.isEmpty()) {
            System.out.println("  - Geçmiş boş");
            return;
        }

        System.out.println("  - Undo geçmişi (son eklenen üstte):");
        stack.forEach(snapshot -> System.out.println("    * " + snapshot.getActionName()));
    }
}
