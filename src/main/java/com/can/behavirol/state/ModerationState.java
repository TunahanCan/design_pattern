package com.can.behavirol.state;

public class ModerationState implements DocumentState {

    @Override
    public String publish(DocumentContext context) {
        if (!"admin".equalsIgnoreCase(context.getCurrentUserRole())) {
            return "Sadece admin moderasyondaki dokümanı yayınlayabilir.";
        }

        context.changeState(new PublishedState());
        return "Doküman yayınlandı.";
    }

    @Override
    public String edit(DocumentContext context, String newContent) {
        context.setContent(newContent);
        return "Moderasyondaki içerik güncellendi, tekrar onay bekliyor.";
    }

    @Override
    public String getName() {
        return "Moderation";
    }
}
