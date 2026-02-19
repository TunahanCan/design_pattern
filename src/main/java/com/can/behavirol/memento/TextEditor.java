package com.can.behavirol.memento;

public class TextEditor {

    private String text;
    private int cursorX;
    private int cursorY;
    private int selectionWidth;

    public TextEditor() {
        this.text = "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCursor(int cursorX, int cursorY) {
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    public void setSelectionWidth(int selectionWidth) {
        this.selectionWidth = selectionWidth;
    }

    public Snapshot createSnapshot(String actionName) {
        return new Snapshot(actionName, text, cursorX, cursorY, selectionWidth);
    }

    public void restore(Snapshot snapshot) {
        this.text = snapshot.text;
        this.cursorX = snapshot.cursorX;
        this.cursorY = snapshot.cursorY;
        this.selectionWidth = snapshot.selectionWidth;
    }

    public String viewState() {
        return "text='" + text + "', cursor=(" + cursorX + "," + cursorY + "), selectionWidth=" + selectionWidth;
    }

    public interface EditorMemento {
        String getActionName();
    }

    public static final class Snapshot implements EditorMemento {

        private final String actionName;
        private final String text;
        private final int cursorX;
        private final int cursorY;
        private final int selectionWidth;

        private Snapshot(String actionName, String text, int cursorX, int cursorY, int selectionWidth) {
            this.actionName = actionName;
            this.text = text;
            this.cursorX = cursorX;
            this.cursorY = cursorY;
            this.selectionWidth = selectionWidth;
        }

        @Override
        public String getActionName() {
            return actionName;
        }
    }
}
