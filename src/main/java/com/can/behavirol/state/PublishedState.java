package com.can.behavirol.state;

public class PublishedState implements DocumentState {

    @Override
    public String publish(DocumentContext context) {
        return "Doküman zaten yayında, işlem yapılmadı.";
    }

    @Override
    public String edit(DocumentContext context, String newContent) {
        return "Yayındaki doküman doğrudan düzenlenemez. Yeni bir taslak oluşturun.";
    }

    @Override
    public String getName() {
        return "Published";
    }
}
