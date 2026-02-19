package com.can.behavirol.command;

public class WriteTextCommand extends AbstractEditorCommand {
    private final String value;

    public WriteTextCommand(ApplicationContext app, Editor editor, String value) {
        super(app, editor);
        this.value = value;
    }

    @Override
    public boolean execute() {
        saveBackup();
        editor.write(value);
        return true;
    }

    @Override
    public String name() {
        return "WriteText";
    }
}
