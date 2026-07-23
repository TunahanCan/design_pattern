package com.can.behavirol.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
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

    /**
     * Caretaker protokolünü tek yerde tutar: mevcut snapshot'ı kaldırır,
     * bir önceki snapshot'ı stack üzerinde bırakıp originator'a geri yükler.
     */
    public boolean undo(TextEditor editor) {
        Objects.requireNonNull(editor, "editor cannot be null");
        if (stack.size() < 2) {
            return false;
        }

        stack.pop();
        editor.restore(stack.peek());
        return true;
    }

    public int size() {
        return stack.size();
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
