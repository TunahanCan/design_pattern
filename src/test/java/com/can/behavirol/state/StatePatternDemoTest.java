package com.can.behavirol.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatePatternDemoTest {

    @Test
    void shouldMoveThroughDraftModerationAndPublishedStates() {
        DocumentContext document = new DocumentContext("ADR", "taslak", "editor");

        assertEquals("Draft", document.getStateName());

        String draftPublishResult = document.publish();
        assertEquals("Taslak moderasyon aşamasına gönderildi.", draftPublishResult);
        assertEquals("Moderation", document.getStateName());

        String moderationPublishResultByEditor = document.publish();
        assertEquals("Sadece admin moderasyondaki dokümanı yayınlayabilir.", moderationPublishResultByEditor);
        assertEquals("Moderation", document.getStateName());

        document.setCurrentUserRole("admin");
        String moderationPublishResultByAdmin = document.publish();
        assertEquals("Doküman yayınlandı.", moderationPublishResultByAdmin);
        assertEquals("Published", document.getStateName());
    }

    @Test
    void shouldPreventEditingWhenPublished() {
        DocumentContext document = new DocumentContext("ADR", "taslak", "admin");

        document.publish();
        document.publish();

        String editResult = document.edit("yeni içerik");

        assertEquals("Yayındaki doküman doğrudan düzenlenemez. Yeni bir taslak oluşturun.", editResult);
        assertEquals("taslak", document.getContent());
    }
}
