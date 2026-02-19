package com.can.behavirol.state;

public class StatePatternDemo {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("7) State");

        DocumentContext document = new DocumentContext(
                "Mimari Karar Dokümanı",
                "İlk taslak içerik",
                "editor"
        );

        System.out.println("Başlangıç durumu: " + document.getStateName());
        System.out.println(document.edit("Güncellenmiş taslak içerik"));
        System.out.println(document.publish());
        System.out.println("Güncel durum: " + document.getStateName());

        System.out.println(document.publish());
        System.out.println("Güncel durum: " + document.getStateName());

        document.setCurrentUserRole("admin");
        System.out.println("Kullanıcı rolü admin oldu.");
        System.out.println(document.publish());
        System.out.println("Güncel durum: " + document.getStateName());

        System.out.println(document.edit("Yayındaki içerik değişikliği"));
        System.out.println(document.publish());
        System.out.println();
    }
}
