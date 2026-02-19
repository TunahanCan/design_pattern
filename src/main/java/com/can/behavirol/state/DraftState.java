package com.can.behavirol.state;

public class DraftState implements DocumentState {

    @Override
    public String publish(DocumentContext context) {
        context.changeState(new ModerationState());
        return "Taslak moderasyon aşamasına gönderildi.";
    }

    @Override
    public String edit(DocumentContext context, String newContent) {
        context.setContent(newContent);
        return "Taslak içerik güncellendi.";
    }

    @Override
    public String getName() {
        return "Draft";
    }
}
