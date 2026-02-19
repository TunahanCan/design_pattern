package com.can.behavirol.command;

public class CommandPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("2) Command");

        ApplicationContext app = new ApplicationContext();
        Editor editor = new Editor();
        CommandHistory history = new CommandHistory();
        EditorToolbar toolbar = new EditorToolbar(history);

        toolbar.setButton("writeHello", new WriteTextCommand(app, editor, "Merhaba "));
        toolbar.setButton("writeWorld", new WriteTextCommand(app, editor, "Command Pattern!"));
        toolbar.setButton("copy", new CopyCommand(app, editor));
        toolbar.setButton("paste", new PasteCommand(app, editor));
        toolbar.setButton("undo", new UndoCommand(toolbar));

        toolbar.click("writeHello");
        toolbar.click("writeWorld");
        System.out.println("Editör: " + editor.getText());

        toolbar.click("copy");
        toolbar.click("paste");
        System.out.println("Paste sonrası: " + editor.getText());

        toolbar.click("undo");
        System.out.println("Undo sonrası: " + editor.getText());
        System.out.println();
    }
}
