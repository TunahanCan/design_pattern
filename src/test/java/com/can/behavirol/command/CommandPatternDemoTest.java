package com.can.behavirol.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Command — editör araç çubuğu")
class CommandPatternDemoTest {

    @Nested
    @DisplayName("Komutlar araç çubuğundan çalıştırıldığında")
    class CommandExecution {

        @Test
        @DisplayName("write komutları receiver üzerindeki metni sırayla büyütür")
        void writeCommandsAppendTextInOrder() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.toolbar.setButton("writeA", new WriteTextCommand(fixture.app, fixture.editor, "A"));
            fixture.toolbar.setButton("writeB", new WriteTextCommand(fixture.app, fixture.editor, "B"));

            // Act
            fixture.toolbar.click("writeA");
            fixture.toolbar.click("writeB");

            // Assert
            assertAll(
                    () -> assertEquals("AB", fixture.editor.getText()),
                    () -> assertEquals(2, fixture.history.size())
            );
        }

        @Test
        @DisplayName("tanımsız buton açık bir IllegalArgumentException üretir")
        void undefinedButtonIsRejected() {
            // Arrange
            Fixture fixture = new Fixture();

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> fixture.toolbar.click("missing")
            );

            // Assert
            assertEquals("Tanımsız buton: missing", error.getMessage());
        }
    }

    @Nested
    @DisplayName("Copy ve paste birlikte kullanıldığında")
    class ClipboardCommands {

        @Test
        @DisplayName("copy editörün tamamını clipboard'a alır ve history'yi büyütmez")
        void copyChangesClipboardButIsNotTracked() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.editor.write("Merhaba");
            fixture.toolbar.setButton("copy", new CopyCommand(fixture.app, fixture.editor));

            // Act
            fixture.toolbar.click("copy");

            // Assert
            assertAll(
                    () -> assertEquals("Merhaba", fixture.app.getClipboard()),
                    () -> assertEquals(0, fixture.history.size())
            );
        }

        @Test
        @DisplayName("dolu clipboard paste edilir ve undo edilebilir olarak history'ye girer")
        void nonBlankClipboardCanBePasted() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.editor.write("A");
            fixture.app.setClipboard("B");
            fixture.toolbar.setButton("paste", new PasteCommand(fixture.app, fixture.editor));

            // Act
            fixture.toolbar.click("paste");

            // Assert
            assertAll(
                    () -> assertEquals("AB", fixture.editor.getText()),
                    () -> assertEquals(1, fixture.history.size())
            );
        }

        @Test
        @DisplayName("boş clipboard paste işlemini ve history kaydını atlar")
        void blankClipboardIsANoOp() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.editor.write("A");
            fixture.app.setClipboard("   ");
            fixture.toolbar.setButton("paste", new PasteCommand(fixture.app, fixture.editor));

            // Act
            fixture.toolbar.click("paste");

            // Assert
            assertAll(
                    () -> assertEquals("A", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size())
            );
        }
    }

    @Nested
    @DisplayName("History üzerinden geri alma yapıldığında")
    class UndoHistory {

        @Test
        @DisplayName("son undo edilebilir komut önce geri alınır")
        void undoUsesLastInFirstOutOrder() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.toolbar.setButton("writeA", new WriteTextCommand(fixture.app, fixture.editor, "A"));
            fixture.toolbar.setButton("writeB", new WriteTextCommand(fixture.app, fixture.editor, "B"));
            fixture.toolbar.setButton("undo", new UndoCommand(fixture.toolbar));
            fixture.toolbar.click("writeA");
            fixture.toolbar.click("writeB");

            // Act
            fixture.toolbar.click("undo");

            // Assert
            assertAll(
                    () -> assertEquals("A", fixture.editor.getText()),
                    () -> assertEquals(1, fixture.history.size())
            );
        }

        @Test
        @DisplayName("farklı komut nesneleriyle iki undo başlangıç durumuna döner")
        void repeatedUndoRestoresEarlierBackups() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.toolbar.setButton("writeA", new WriteTextCommand(fixture.app, fixture.editor, "A"));
            fixture.toolbar.setButton("writeB", new WriteTextCommand(fixture.app, fixture.editor, "B"));
            fixture.toolbar.setButton("undo", new UndoCommand(fixture.toolbar));
            fixture.toolbar.click("writeA");
            fixture.toolbar.click("writeB");

            // Act
            fixture.toolbar.click("undo");
            fixture.toolbar.click("undo");

            // Assert
            assertAll(
                    () -> assertEquals("", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size())
            );
        }

        @Test
        @DisplayName("boş history üzerinde undo güvenli bir no-op'tur")
        void undoOnEmptyHistoryDoesNothing() {
            // Arrange
            Fixture fixture = new Fixture();
            fixture.editor.write("Korunacak içerik");
            fixture.toolbar.setButton("undo", new UndoCommand(fixture.toolbar));

            // Act
            fixture.toolbar.click("undo");

            // Assert
            assertAll(
                    () -> assertEquals("Korunacak içerik", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size())
            );
        }
    }

    @Nested
    @DisplayName("Aynı stateful komut nesnesi tekrar çalıştırıldığında")
    class ReusableCommandHistory {

        @Test
        @DisplayName("her execute kendi backup'ını saklar ve iki undo iki adımı geri alır")
        void repeatedExecutionKeepsASeparateBackupPerInvocation() {
            // Arrange
            Fixture fixture = new Fixture();
            WriteTextCommand reusableCommand =
                    new WriteTextCommand(fixture.app, fixture.editor, "A");
            fixture.toolbar.setButton("writeA", reusableCommand);
            fixture.toolbar.setButton("undo", new UndoCommand(fixture.toolbar));
            fixture.toolbar.click("writeA");
            fixture.toolbar.click("writeA");

            // Act
            fixture.toolbar.click("undo");
            String afterFirstUndo = fixture.editor.getText();
            fixture.toolbar.click("undo");

            // Assert
            assertAll(
                    () -> assertEquals("A", afterFirstUndo),
                    () -> assertEquals("", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size())
            );
        }
    }

    @Nested
    @DisplayName("Komutlar macro altında tek kullanıcı niyeti olarak gruplanınca")
    class MacroCommands {

        @Test
        @DisplayName("birden fazla receiver çağrısı history'de tek kayıt olur ve birlikte geri alınır")
        void macroIsTrackedAndUndoneAsOneUnit() {
            // Arrange
            Fixture fixture = new Fixture();
            MacroCommand greeting = new MacroCommand(
                    "Selamlama yaz",
                    List.of(
                            new WriteTextCommand(fixture.app, fixture.editor, "Merhaba "),
                            new WriteTextCommand(fixture.app, fixture.editor, "dünya")
                    )
            );
            fixture.toolbar.setButton("greet", greeting);
            fixture.toolbar.setButton("undo", new UndoCommand(fixture.toolbar));

            // Act
            fixture.toolbar.click("greet");
            String afterMacro = fixture.editor.getText();
            fixture.toolbar.click("undo");

            // Assert
            assertAll(
                    () -> assertEquals("Merhaba dünya", afterMacro),
                    () -> assertEquals("", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size()),
                    () -> assertEquals("Selamlama yaz", greeting.name())
            );
        }

        @Test
        @DisplayName("alt komut hata verirse daha önce çalışan geri alınabilir komutlar telafi edilir")
        void macroCompensatesAlreadyExecutedCommandsOnFailure() {
            // Arrange
            Fixture fixture = new Fixture();
            Command failingCommand = new Command() {
                @Override
                public boolean execute() {
                    throw new IllegalStateException("Yazma servisi kullanılamıyor");
                }

                @Override
                public void undo() {
                }

                @Override
                public String name() {
                    return "Hatalı";
                }
            };
            MacroCommand macro = new MacroCommand(
                    "Atomic görünüm",
                    List.of(
                            new WriteTextCommand(fixture.app, fixture.editor, "geçici"),
                            failingCommand
                    )
            );
            fixture.toolbar.setButton("macro", macro);

            // Act
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    () -> fixture.toolbar.click("macro")
            );

            // Assert
            assertAll(
                    () -> assertEquals("Yazma servisi kullanılamıyor", error.getMessage()),
                    () -> assertEquals("", fixture.editor.getText()),
                    () -> assertEquals(0, fixture.history.size()),
                    () -> assertTrue(fixture.app.getClipboard().isEmpty())
            );
        }

        @Test
        @DisplayName("execute hatası korunur, bütün rollback'ler denenir ve rollback hataları suppressed olur")
        void executionFailureRemainsPrimaryWhenMultipleRollbacksFail() {
            // Arrange
            List<String> undoOrder = new ArrayList<>();
            IllegalStateException firstUndoFailure =
                    new IllegalStateException("birinci undo başarısız");
            IllegalStateException secondUndoFailure =
                    new IllegalStateException("ikinci undo başarısız");
            IllegalArgumentException executeFailure =
                    new IllegalArgumentException("üçüncü execute başarısız");
            MacroCommand macro = new MacroCommand(
                    "Hata telafisi",
                    List.of(
                            new ControlledCommand(
                                    "birinci",
                                    true,
                                    null,
                                    firstUndoFailure,
                                    undoOrder
                            ),
                            new ControlledCommand(
                                    "ikinci",
                                    true,
                                    null,
                                    secondUndoFailure,
                                    undoOrder
                            ),
                            new ControlledCommand(
                                    "üçüncü",
                                    false,
                                    executeFailure,
                                    null,
                                    undoOrder
                            )
                    )
            );

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    macro::execute
            );

            // Assert
            assertAll(
                    () -> assertSame(executeFailure, error),
                    () -> assertEquals(List.of("ikinci", "birinci"), undoOrder),
                    () -> assertArrayEquals(
                            new Throwable[]{secondUndoFailure, firstUndoFailure},
                            error.getSuppressed()
                    )
            );
        }

        @Test
        @DisplayName("normal undo bütün alt komutları dener ve ilk undo hatasını primary yapar")
        void normalUndoAggregatesFailuresInDeterministicReverseOrder() {
            // Arrange
            List<String> undoOrder = new ArrayList<>();
            IllegalStateException secondUndoFailure =
                    new IllegalStateException("ikinci undo başarısız");
            IllegalStateException thirdUndoFailure =
                    new IllegalStateException("üçüncü undo başarısız");
            MacroCommand macro = new MacroCommand(
                    "Deterministik undo",
                    List.of(
                            new ControlledCommand("birinci", true, null, null, undoOrder),
                            new ControlledCommand(
                                    "ikinci",
                                    true,
                                    null,
                                    secondUndoFailure,
                                    undoOrder
                            ),
                            new ControlledCommand(
                                    "üçüncü",
                                    true,
                                    null,
                                    thirdUndoFailure,
                                    undoOrder
                            )
                    )
            );
            macro.execute();

            // Act
            IllegalStateException error = assertThrows(
                    IllegalStateException.class,
                    macro::undo
            );
            macro.undo();

            // Assert
            assertAll(
                    () -> assertSame(thirdUndoFailure, error),
                    () -> assertEquals(List.of("üçüncü", "ikinci", "birinci"), undoOrder),
                    () -> assertArrayEquals(
                            new Throwable[]{secondUndoFailure},
                            error.getSuppressed()
                    )
            );
        }
    }

    private static final class ControlledCommand implements Command {
        private final String commandName;
        private final boolean tracked;
        private final RuntimeException executeFailure;
        private final RuntimeException undoFailure;
        private final List<String> undoOrder;

        private ControlledCommand(String commandName,
                                  boolean tracked,
                                  RuntimeException executeFailure,
                                  RuntimeException undoFailure,
                                  List<String> undoOrder) {
            this.commandName = commandName;
            this.tracked = tracked;
            this.executeFailure = executeFailure;
            this.undoFailure = undoFailure;
            this.undoOrder = undoOrder;
        }

        @Override
        public boolean execute() {
            if (executeFailure != null) {
                throw executeFailure;
            }
            return tracked;
        }

        @Override
        public void undo() {
            undoOrder.add(commandName);
            if (undoFailure != null) {
                throw undoFailure;
            }
        }

        @Override
        public String name() {
            return commandName;
        }
    }

    private static final class Fixture {
        private final ApplicationContext app = new ApplicationContext();
        private final Editor editor = new Editor();
        private final CommandHistory history = new CommandHistory();
        private final EditorToolbar toolbar = new EditorToolbar(history);
    }
}
