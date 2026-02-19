package com.can.behavirol.command;

public class PasteCommand extends AbstractEditorCommand {

    public PasteCommand(ApplicationContext app, Editor editor) {
        super(app, editor);
    }

    @Override
    public boolean execute() {
        if (app.getClipboard().isBlank()) {
            return false;
        }

        saveBackup();
        editor.write(app.getClipboard());
        return true;
    }

    @Override
    public String name() {
        return "Paste";
    }
}
