package com.can.behavirol.memento;

public class MementoPatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("5) Memento");

        TextEditor editor = new TextEditor();
        EditorHistory history = new EditorHistory();

        history.push(editor.createSnapshot("Başlangıç"));

        editor.setText("Memento pattern");
        editor.setCursor(7, 1);
        editor.setSelectionWidth(7);
        history.push(editor.createSnapshot("Başlık yazıldı"));
        System.out.println("İlk düzenleme: " + editor.viewState());

        editor.setText("Memento pattern ile undo işlemi");
        editor.setCursor(20, 1);
        editor.setSelectionWidth(5);
        history.push(editor.createSnapshot("Undo açıklaması yazıldı"));
        System.out.println("İkinci düzenleme: " + editor.viewState());

        history.popLast(); // mevcut durumun snapshot'ı
        history.popLast().ifPresent(editor::restore);
        System.out.println("Undo sonrası: " + editor.viewState());

        history.printTimeline();
        System.out.println();
    }
}
