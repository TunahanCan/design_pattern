package com.can.behavirol.command;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstractEditorCommand implements Command {
    protected final ApplicationContext app;
    protected final Editor editor;
    private final Deque<String> backups = new ArrayDeque<>();

    protected AbstractEditorCommand(ApplicationContext app, Editor editor) {
        this.app = app;
        this.editor = editor;
    }

    protected void saveBackup() {
        backups.push(editor.getText());
    }

    @Override
    public void undo() {
        String backup = backups.poll();
        if (backup != null) {
            editor.replaceAll(backup);
        }
    }
}
