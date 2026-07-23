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
    public String reject(DocumentContext context, String reason) {
        if (!"admin".equalsIgnoreCase(context.getCurrentUserRole())) {
            return "Sadece admin moderasyondaki dokümanı reddedebilir.";
        }

        if (reason == null || reason.isBlank()) {
            return "Reddetme nedeni zorunludur.";
        }

        context.recordReviewNote(reason.trim());
        context.changeState(new DraftState());
        return "Doküman düzeltme için taslağa geri gönderildi.";
    }

    @Override
    public String getName() {
        return "Moderation";
    }
}
