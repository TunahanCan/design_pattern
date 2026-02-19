package com.can.behavirol.command;

public class CopyCommand extends AbstractEditorCommand {

    public CopyCommand(ApplicationContext app, Editor editor) {
        super(app, editor);
    }

    @Override
    public boolean execute() {
        app.setClipboard(editor.getText());
        return false;
    }

    @Override
    public String name() {
        return "Copy";
    }
}
