package com.can.behavirol.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandPatternDemoTest {

    @Test
    void shouldExecuteCommandsViaToolbarAndSupportUndo() {
        ApplicationContext app = new ApplicationContext();
        Editor editor = new Editor();
        CommandHistory history = new CommandHistory();
        EditorToolbar toolbar = new EditorToolbar(history);

        toolbar.setButton("writeA", new WriteTextCommand(app, editor, "A"));
        toolbar.setButton("writeB", new WriteTextCommand(app, editor, "B"));
        toolbar.setButton("copy", new CopyCommand(app, editor));
        toolbar.setButton("paste", new PasteCommand(app, editor));
        toolbar.setButton("undo", new UndoCommand(toolbar));

        toolbar.click("writeA");
        toolbar.click("writeB");
        toolbar.click("copy");
        toolbar.click("paste");

        assertEquals("ABAB", editor.getText());
        assertEquals(3, history.size());

        toolbar.click("undo");

        assertEquals("AB", editor.getText());
        assertEquals(2, history.size());
    }
}
