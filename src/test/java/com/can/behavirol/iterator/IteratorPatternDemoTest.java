package com.can.behavirol.iterator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Iterator — sosyal grafik gezintisi")
class IteratorPatternDemoTest {

    @Nested
    @DisplayName("İlişki türüne göre iterator üretildiğinde")
    class RelationTraversal {

        @Test
        @DisplayName("friends iterator listedeki ilişki sırasını korur")
        void friendsAreReturnedInGraphOrder() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator iterator = fixture.network.createFriendsIterator(fixture.ali.id());

            // Act
            List<String> emails = new SocialSpammer().send(iterator, "kampanya");

            // Assert
            assertEquals(
                    List.of("ayse@acme.com", "mehmet@globex.com", "zeynep@acme.com"),
                    emails
            );
        }

        @Test
        @DisplayName("coworkers iterator yalnız iş arkadaşı ilişkilerini döndürür")
        void coworkersAreFilteredByTheirOwnRelation() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator iterator = fixture.network.createCoworkersIterator(fixture.ali.id());

            // Act
            List<String> emails = new SocialSpammer().send(iterator, "duyuru");

            // Assert
            assertEquals(List.of("ayse@acme.com", "zeynep@acme.com"), emails);
        }
    }

    @Nested
    @DisplayName("Iterator kendi cursor durumunu taşırken")
    class CursorState {

        @Test
        @DisplayName("hasMore cursor'u ilerletmez, getNext bir eleman ilerletir")
        void hasMoreIsIdempotentAndGetNextAdvances() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator iterator = fixture.network.createFriendsIterator(fixture.ali.id());

            // Act
            boolean firstCheck = iterator.hasMore();
            boolean secondCheck = iterator.hasMore();
            Profile firstProfile = iterator.getNext();

            // Assert
            assertAll(
                    () -> assertTrue(firstCheck),
                    () -> assertTrue(secondCheck),
                    () -> assertEquals("2", firstProfile.id())
            );
        }

        @Test
        @DisplayName("aynı koleksiyondan üretilen iki iterator birbirini ilerletmez")
        void iteratorsHaveIndependentPositions() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator first = fixture.network.createFriendsIterator(fixture.ali.id());
            ProfileIterator second = fixture.network.createFriendsIterator(fixture.ali.id());

            // Act
            Profile firstIteratorFirstItem = first.getNext();
            first.getNext();
            Profile secondIteratorFirstItem = second.getNext();

            // Assert
            assertAll(
                    () -> assertEquals("2", firstIteratorFirstItem.id()),
                    () -> assertEquals("2", secondIteratorFirstItem.id())
            );
        }

        @Test
        @DisplayName("iterator bittiğinde hasMore false, getNext null döner")
        void exhaustedIteratorUsesNullSentinel() {
            // Arrange
            Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");
            SocialGraph graph = new SocialGraph(
                    Map.of(ali.id(), ali),
                    Map.of(ali.id(), List.of()),
                    Map.of(ali.id(), List.of())
            );
            ProfileIterator iterator = new Facebook(graph).createFriendsIterator(ali.id());

            // Act
            boolean hasMore = iterator.hasMore();
            Profile next = iterator.getNext();

            // Assert
            assertAll(
                    () -> assertFalse(hasMore),
                    () -> assertNull(next)
            );
        }
    }

    @Nested
    @DisplayName("Grafikte ilişki bulunmadığında")
    class EmptyTraversal {

        @Test
        @DisplayName("bilinmeyen profil kimliği boş iterator üretir")
        void unknownProfileHasNoRelations() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator iterator = fixture.network.createFriendsIterator("unknown");

            // Act
            List<String> sentEmails = new SocialSpammer().send(iterator, "mesaj");

            // Assert
            assertAll(
                    () -> assertTrue(sentEmails.isEmpty()),
                    () -> assertFalse(iterator.hasMore())
            );
        }

        @Test
        @DisplayName("grafikte olmayan ilişki kimlikleri sessizce filtrelenir")
        void danglingRelationIdsAreSkipped() {
            // Arrange
            Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");
            SocialGraph graph = new SocialGraph(
                    Map.of(ali.id(), ali),
                    Map.of(ali.id(), List.of("missing")),
                    Map.of()
            );
            ProfileIterator iterator = new Facebook(graph).createFriendsIterator(ali.id());

            // Act
            boolean hasMore = iterator.hasMore();

            // Assert
            assertFalse(hasMore);
        }
    }

    @Nested
    @DisplayName("Iterator başka bir iterator ile lazy olarak süzüldüğünde")
    class IteratorComposition {

        @Test
        @DisplayName("şirket filtresi yalnız hedef şirketteki arkadaşları üretir")
        void filtersProfilesWithoutExposingTheGraph() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator acmeFriends = new CompanyProfileIterator(
                    fixture.network.createFriendsIterator(fixture.ali.id()),
                    "acme"
            );

            // Act
            List<String> recipients = new SocialSpammer().send(acmeFriends, "Acme buluşması");

            // Assert
            assertEquals(
                    List.of("ayse@acme.com", "zeynep@acme.com"),
                    recipients
            );
        }

        @Test
        @DisplayName("tekrarlanan hasMore çağrıları buffered sonucu tüketmez")
        void repeatedHasMoreDoesNotLoseTheBufferedMatch() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator iterator = new CompanyProfileIterator(
                    fixture.network.createFriendsIterator(fixture.ali.id()),
                    "Globex"
            );

            // Act
            boolean firstCheck = iterator.hasMore();
            boolean secondCheck = iterator.hasMore();
            Profile match = iterator.getNext();

            // Assert
            assertAll(
                    () -> assertTrue(firstCheck),
                    () -> assertTrue(secondCheck),
                    () -> assertEquals("mehmet@globex.com", match.email()),
                    () -> assertFalse(iterator.hasMore())
            );
        }
    }

    private record Fixture(Profile ali, SocialNetwork network) {

        private static Fixture standard() {
            Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");
            Profile ayse = new Profile("2", "Ayşe", "ayse@acme.com", "Acme");
            Profile mehmet = new Profile("3", "Mehmet", "mehmet@globex.com", "Globex");
            Profile zeynep = new Profile("4", "Zeynep", "zeynep@acme.com", "Acme");
            SocialGraph graph = new SocialGraph(
                    Map.of(
                            ali.id(), ali,
                            ayse.id(), ayse,
                            mehmet.id(), mehmet,
                            zeynep.id(), zeynep
                    ),
                    Map.of(ali.id(), List.of(ayse.id(), mehmet.id(), zeynep.id())),
                    Map.of(ali.id(), List.of(ayse.id(), zeynep.id()))
            );
            return new Fixture(ali, new Facebook(graph));
        }
    }
}
