package com.can.behavirol.state;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("State — doküman yayın yaşam döngüsü")
class StatePatternDemoTest {

    @Nested
    @DisplayName("DocumentContext oluşturulduğunda")
    class InitialState {

        @Test
        @DisplayName("başlangıç state'i Draft ve başlangıç içeriği korunur")
        void documentStartsAsDraft() {
            // Arrange
            DocumentContext document = new DocumentContext("ADR", "taslak", "editor");

            // Act
            String stateName = document.getStateName();

            // Assert
            assertAll(
                    () -> assertEquals("Draft", stateName),
                    () -> assertEquals("taslak", document.getContent()),
                    () -> assertEquals("ADR", document.getTitle())
            );
        }
    }

    @Nested
    @DisplayName("Doküman Draft state'indeyken")
    class DraftBehavior {

        @Test
        @DisplayName("edit içeriği değiştirir fakat state'i korur")
        void draftCanBeEdited() {
            // Arrange
            DocumentContext document = new DocumentContext("ADR", "taslak", "editor");

            // Act
            String result = document.edit("güncel taslak");

            // Assert
            assertAll(
                    () -> assertEquals("Taslak içerik güncellendi.", result),
                    () -> assertEquals("güncel taslak", document.getContent()),
                    () -> assertEquals("Draft", document.getStateName())
            );
        }

        @Test
        @DisplayName("publish dokümanı Moderation state'ine geçirir")
        void publishSubmitsDraftForModeration() {
            // Arrange
            DocumentContext document = new DocumentContext("ADR", "taslak", "editor");

            // Act
            String result = document.publish();

            // Assert
            assertAll(
                    () -> assertEquals("Taslak moderasyon aşamasına gönderildi.", result),
                    () -> assertEquals("Moderation", document.getStateName())
            );
        }
    }

    @Nested
    @DisplayName("Doküman Moderation state'indeyken")
    class ModerationBehavior {

        @Test
        @DisplayName("editor rolü publish yapamaz ve state değişmez")
        void editorCannotPublishModeratedDocument() {
            // Arrange
            DocumentContext document = documentInModeration("editor");

            // Act
            String result = document.publish();

            // Assert
            assertAll(
                    () -> assertEquals(
                            "Sadece admin moderasyondaki dokümanı yayınlayabilir.",
                            result
                    ),
                    () -> assertEquals("Moderation", document.getStateName())
            );
        }

        @Test
        @DisplayName("admin rolü dokümanı Published state'ine geçirir")
        void adminCanPublishModeratedDocument() {
            // Arrange
            DocumentContext document = documentInModeration("ADMIN");

            // Act
            String result = document.publish();

            // Assert
            assertAll(
                    () -> assertEquals("Doküman yayınlandı.", result),
                    () -> assertEquals("Published", document.getStateName())
            );
        }

        @Test
        @DisplayName("edit içeriği günceller ve Moderation state'ini korur")
        void moderatedDocumentCanBeEdited() {
            // Arrange
            DocumentContext document = documentInModeration("editor");

            // Act
            String result = document.edit("inceleme sonrası içerik");

            // Assert
            assertAll(
                    () -> assertEquals(
                            "Moderasyondaki içerik güncellendi, tekrar onay bekliyor.",
                            result
                    ),
                    () -> assertEquals("inceleme sonrası içerik", document.getContent()),
                    () -> assertEquals("Moderation", document.getStateName())
            );
        }
    }

    @Nested
    @DisplayName("Doküman Published state'indeyken")
    class PublishedBehavior {

        @Test
        @DisplayName("edit isteği reddedilir ve mevcut içerik korunur")
        void publishedDocumentCannotBeEdited() {
            // Arrange
            DocumentContext document = publishedDocument();

            // Act
            String result = document.edit("yeni içerik");

            // Assert
            assertAll(
                    () -> assertEquals(
                            "Yayındaki doküman doğrudan düzenlenemez. Yeni bir taslak oluşturun.",
                            result
                    ),
                    () -> assertEquals("taslak", document.getContent()),
                    () -> assertEquals("Published", document.getStateName())
            );
        }

        @Test
        @DisplayName("tekrar publish idempotent bir no-op mesajı döndürür")
        void publishingAgainDoesNotChangeState() {
            // Arrange
            DocumentContext document = publishedDocument();

            // Act
            String result = document.publish();

            // Assert
            assertAll(
                    () -> assertEquals("Doküman zaten yayında, işlem yapılmadı.", result),
                    () -> assertEquals("Published", document.getStateName())
            );
        }
    }

    @Nested
    @DisplayName("Moderasyon dokümanı düzeltme için reddettiğinde")
    class ReviewRejection {

        @Test
        @DisplayName("gerekçeli ret dokümanı Draft'a döndürür ve inceleme notunu saklar")
        void rejectionReturnsDocumentToDraftWithReviewNote() {
            // Arrange
            DocumentContext document = documentInModeration("admin");

            // Act
            String result = document.reject("  Kaynak bağlantısı eksik.  ");

            // Assert
            assertAll(
                    () -> assertEquals(
                            "Doküman düzeltme için taslağa geri gönderildi.",
                            result
                    ),
                    () -> assertEquals("Draft", document.getStateName()),
                    () -> assertEquals("Kaynak bağlantısı eksik.", document.getLastReviewNote())
            );
        }

        @Test
        @DisplayName("boş ret gerekçesi transition üretmez")
        void blankReasonKeepsDocumentInModeration() {
            // Arrange
            DocumentContext document = documentInModeration("admin");

            // Act
            String result = document.reject("   ");

            // Assert
            assertAll(
                    () -> assertEquals("Reddetme nedeni zorunludur.", result),
                    () -> assertEquals("Moderation", document.getStateName()),
                    () -> assertNull(document.getLastReviewNote())
            );
        }

        @Test
        @DisplayName("admin olmayan kullanıcı ret transition'ını başlatamaz")
        void nonAdminCannotRejectModeratedDocument() {
            // Arrange
            DocumentContext document = documentInModeration("editor");

            // Act
            String result = document.reject("Kaynak eksik");

            // Assert
            assertAll(
                    () -> assertEquals(
                            "Sadece admin moderasyondaki dokümanı reddedebilir.",
                            result
                    ),
                    () -> assertEquals("Moderation", document.getStateName()),
                    () -> assertNull(document.getLastReviewNote())
            );
        }

        @Test
        @DisplayName("Published state ortak default davranışla ret isteğini reddeder")
        void publishedDocumentCannotBeRejected() {
            // Arrange
            DocumentContext document = publishedDocument();

            // Act
            String result = document.reject("Geç ret");

            // Assert
            assertAll(
                    () -> assertEquals("Bu durumdaki doküman reddedilemez.", result),
                    () -> assertEquals("Published", document.getStateName())
            );
        }
    }

    private static DocumentContext documentInModeration(String role) {
        DocumentContext document = new DocumentContext("ADR", "taslak", role);
        document.publish();
        return document;
    }

    private static DocumentContext publishedDocument() {
        DocumentContext document = documentInModeration("admin");
        document.publish();
        return document;
    }
}
