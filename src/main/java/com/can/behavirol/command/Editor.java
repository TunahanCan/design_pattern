package com.can.behavirol.command;

public class Editor {
    private final StringBuilder text = new StringBuilder();

    public String getText() {
        return text.toString();
    }

    public void write(String value) {
        text.append(value);
    }

    public void deleteLast(int count) {
        if (count <= 0 || text.isEmpty()) {
            return;
        }

        int deleteFrom = Math.max(0, text.length() - count);
        text.delete(deleteFrom, text.length());
    }

    public void replaceAll(String content) {
        text.setLength(0);
        text.append(content);
    }
}
