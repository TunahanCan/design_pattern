package com.can.behavirol.command;

public abstract class AbstractEditorCommand implements Command {
    protected final ApplicationContext app;
    protected final Editor editor;
    private String backup;

    protected AbstractEditorCommand(ApplicationContext app, Editor editor) {
        this.app = app;
        this.editor = editor;
    }

    protected void saveBackup() {
        backup = editor.getText();
    }

    @Override
    public void undo() {
        if (backup != null) {
            editor.replaceAll(backup);
        }
    }
}
