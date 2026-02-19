package com.can.behavirol.command;

public class UndoCommand implements Command {
    private final EditorToolbar toolbar;

    public UndoCommand(EditorToolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public boolean execute() {
        toolbar.undoLast();
        return false;
    }

    @Override
    public void undo() {
        // Undo komutunun kendi geri alması yok.
    }

    @Override
    public String name() {
        return "Undo";
    }
}
