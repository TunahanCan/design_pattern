package com.can.demo.behavioral.command;

import com.can.behavirol.command.ApplicationContext;
import com.can.behavirol.command.CommandHistory;
import com.can.behavirol.command.CopyCommand;
import com.can.behavirol.command.Editor;
import com.can.behavirol.command.EditorToolbar;
import com.can.behavirol.command.MacroCommand;
import com.can.behavirol.command.PasteCommand;
import com.can.behavirol.command.UndoCommand;
import com.can.behavirol.command.WriteTextCommand;

import java.util.List;

public final class CommandPatternDemo {

    private CommandPatternDemo() {
    }

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

        toolbar.setButton(
                "signature",
                new MacroCommand(
                        "İmza ekle",
                        List.of(
                                new WriteTextCommand(app, editor, "\n"),
                                new WriteTextCommand(app, editor, "— Tasarım Ekibi")
                        )
                )
        );
        toolbar.click("signature");
        System.out.println("Macro sonrası: " + editor.getText());
        toolbar.click("undo");
        System.out.println("Macro undo sonrası: " + editor.getText());
        System.out.println();
    }
}
