package com.can.behavirol.memento;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Memento — editör snapshot geçmişi")
class MementoPatternDemoTest {

    @Nested
    @DisplayName("Originator snapshot ürettiğinde")
    class SnapshotCreation {

        @Test
        @DisplayName("snapshot işlem adını ve üretildiği andaki bütün editör durumunu taşır")
        void snapshotCapturesCompleteState() {
            // Arrange
            TextEditor editor = new TextEditor();
            editor.setText("İlk sürüm");
            editor.setCursor(4, 2);
            editor.setSelectionWidth(3);

            // Act
            TextEditor.Snapshot snapshot = editor.createSnapshot("Kaydet");
            editor.setText("Sonraki sürüm");
            editor.setCursor(0, 0);
            editor.setSelectionWidth(0);
            editor.restore(snapshot);

            // Assert
            assertAll(
                    () -> assertEquals("Kaydet", snapshot.getActionName()),
                    () -> assertEquals(
                            "text='İlk sürüm', cursor=(4,2), selectionWidth=3",
                            editor.viewState()
                    )
            );
        }

        @Test
        @DisplayName("snapshot üretildikten sonraki editler yakalanmış durumu değiştirmez")
        void snapshotRemainsIndependentFromLaterChanges() {
            // Arrange
            TextEditor editor = new TextEditor();
            editor.setText("A");
            TextEditor.Snapshot snapshot = editor.createSnapshot("A yazıldı");

            // Act
            editor.setText("B");
            editor.restore(snapshot);

            // Assert
            assertEquals("text='A', cursor=(0,0), selectionWidth=0", editor.viewState());
        }
    }

    @Nested
    @DisplayName("Caretaker snapshot'ları sakladığında")
    class HistoryStack {

        @Test
        @DisplayName("snapshot'lar son giren ilk çıkar sırasıyla alınır")
        void snapshotsArePoppedInLastInFirstOutOrder() {
            // Arrange
            TextEditor editor = new TextEditor();
            EditorHistory history = new EditorHistory();
            history.push(editor.createSnapshot("Birinci"));
            editor.setText("İkinci durum");
            history.push(editor.createSnapshot("İkinci"));

            // Act
            Optional<TextEditor.Snapshot> latest = history.popLast();
            Optional<TextEditor.Snapshot> previous = history.popLast();

            // Assert
            assertAll(
                    () -> assertTrue(latest.isPresent()),
                    () -> assertEquals("İkinci", latest.orElseThrow().getActionName()),
                    () -> assertTrue(previous.isPresent()),
                    () -> assertEquals("Birinci", previous.orElseThrow().getActionName())
            );
        }

        @Test
        @DisplayName("boş history Optional.empty döndürür")
        void emptyHistoryReturnsEmptyOptional() {
            // Arrange
            EditorHistory history = new EditorHistory();

            // Act
            Optional<TextEditor.Snapshot> result = history.popLast();

            // Assert
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Caretaker API'siyle tek adım undo yapıldığında")
    class SingleStepUndo {

        @Test
        @DisplayName("önceki snapshot restore edilirken yeniden undo edilebilmesi için history'de kalır")
        void restoredSnapshotRemainsInHistory() {
            // Arrange
            TextEditor editor = new TextEditor();
            EditorHistory history = new EditorHistory();
            history.push(editor.createSnapshot("Başlangıç"));
            editor.setText("Birinci düzenleme");
            editor.setCursor(5, 1);
            editor.setSelectionWidth(2);
            history.push(editor.createSnapshot("Birinci"));
            editor.setText("İkinci düzenleme");
            editor.setCursor(9, 2);
            editor.setSelectionWidth(4);
            history.push(editor.createSnapshot("İkinci"));

            // Act
            boolean undone = history.undo(editor);

            // Assert
            assertAll(
                    () -> assertTrue(undone),
                    () -> assertEquals(
                            "text='Birinci düzenleme', cursor=(5,1), selectionWidth=2",
                            editor.viewState()
                    ),
                    () -> assertEquals(2, history.size())
            );
        }
    }

    @Nested
    @DisplayName("Caretaker undo protokolünü yönettiğinde")
    class EncapsulatedUndo {

        @Test
        @DisplayName("ardışık undo çağrıları originator'ı adım adım geçmişe taşır")
        void repeatedUndoRestoresEachPreviousSnapshot() {
            // Arrange
            TextEditor editor = new TextEditor();
            EditorHistory history = new EditorHistory();
            history.push(editor.createSnapshot("Başlangıç"));
            editor.setText("A");
            history.push(editor.createSnapshot("A"));
            editor.setText("AB");
            history.push(editor.createSnapshot("AB"));

            // Act
            boolean firstUndo = history.undo(editor);
            String afterFirstUndo = editor.viewState();
            boolean secondUndo = history.undo(editor);

            // Assert
            assertAll(
                    () -> assertTrue(firstUndo),
                    () -> assertEquals(
                            "text='A', cursor=(0,0), selectionWidth=0",
                            afterFirstUndo
                    ),
                    () -> assertTrue(secondUndo),
                    () -> assertEquals(
                            "text='', cursor=(0,0), selectionWidth=0",
                            editor.viewState()
                    ),
                    () -> assertEquals(1, history.size())
            );
        }

        @Test
        @DisplayName("tek snapshot kaldığında undo false döner ve başlangıç korunur")
        void undoStopsAtTheOldestSnapshot() {
            // Arrange
            TextEditor editor = new TextEditor();
            EditorHistory history = new EditorHistory();
            history.push(editor.createSnapshot("Başlangıç"));

            // Act
            boolean undone = history.undo(editor);

            // Assert
            assertAll(
                    () -> assertFalse(undone),
                    () -> assertEquals(1, history.size()),
                    () -> assertEquals(
                            "text='', cursor=(0,0), selectionWidth=0",
                            editor.viewState()
                    )
            );
        }

        @Test
        @DisplayName("null editor history değişmeden önce reddedilir ve sonraki undo korunur")
        void nullEditorDoesNotConsumeTheLatestSnapshot() {
            // Arrange
            TextEditor editor = new TextEditor();
            EditorHistory history = new EditorHistory();
            history.push(editor.createSnapshot("Başlangıç"));
            editor.setText("A");
            history.push(editor.createSnapshot("A"));

            // Act
            NullPointerException error = assertThrows(
                    NullPointerException.class,
                    () -> history.undo(null)
            );
            int sizeAfterFailure = history.size();
            String stateAfterFailure = editor.viewState();
            boolean validUndo = history.undo(editor);

            // Assert
            assertAll(
                    () -> assertEquals("editor cannot be null", error.getMessage()),
                    () -> assertEquals(2, sizeAfterFailure),
                    () -> assertEquals(
                            "text='A', cursor=(0,0), selectionWidth=0",
                            stateAfterFailure
                    ),
                    () -> assertTrue(validUndo),
                    () -> assertEquals(1, history.size()),
                    () -> assertEquals(
                            "text='', cursor=(0,0), selectionWidth=0",
                            editor.viewState()
                    )
            );
        }
    }
}
