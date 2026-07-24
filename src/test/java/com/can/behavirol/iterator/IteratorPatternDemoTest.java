package com.can.behavirol.iterator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

        @Test
        @DisplayName("şirket ölçütü trim edilir, blank ölçüt ise sınırda reddedilir")
        void companyFilterNormalizesAndValidatesItsCriterion() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator trimmedFilter = new CompanyProfileIterator(
                    fixture.network.createFriendsIterator(fixture.ali.id()),
                    "  acme  "
            );

            // Act
            List<String> recipients = new SocialSpammer().send(trimmedFilter, "buluşma");
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> new CompanyProfileIterator(
                            fixture.network.createFriendsIterator(fixture.ali.id()),
                            "   "
                    )
            );

            // Assert
            assertAll(
                    () -> assertEquals(
                            List.of("ayse@acme.com", "zeynep@acme.com"),
                            recipients
                    ),
                    () -> assertEquals("company cannot be blank", error.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Custom iterator standart Java Iterator görünümüne uyarlandığında")
    class StandardIteratorCompatibility {

        @Test
        @DisplayName("standart next bitişte null yerine NoSuchElementException üretir")
        void standardAdapterUsesJavaExhaustionContract() {
            // Arrange
            Fixture fixture = Fixture.standard();
            Iterator<Profile> iterator = fixture.network
                    .createCoworkersIterator(fixture.ali.id())
                    .asJavaIterator();

            // Act
            Profile first = iterator.next();
            Profile second = iterator.next();
            NoSuchElementException error = assertThrows(
                    NoSuchElementException.class,
                    iterator::next
            );

            // Assert
            assertAll(
                    () -> assertEquals("2", first.id()),
                    () -> assertEquals("4", second.id()),
                    () -> assertFalse(iterator.hasNext()),
                    () -> assertEquals("No more profiles", error.getMessage())
            );
        }

        @Test
        @DisplayName("adapter yeni traversal açmaz ve delegate ile aynı cursor'ı tüketir")
        void adapterSharesTheDelegateCursor() {
            // Arrange
            Fixture fixture = Fixture.standard();
            ProfileIterator custom = fixture.network.createFriendsIterator(fixture.ali.id());
            Iterator<Profile> standard = custom.asJavaIterator();

            // Act
            Profile consumedThroughStandardApi = standard.next();
            Profile consumedThroughCustomApi = custom.getNext();

            // Assert
            assertAll(
                    () -> assertEquals("2", consumedThroughStandardApi.id()),
                    () -> assertEquals("3", consumedThroughCustomApi.id())
            );
        }
    }

    @Nested
    @DisplayName("SocialGraph aggregate'i oluşturulduğunda")
    class AggregateSnapshot {

        @Test
        @DisplayName("constructor girdilerinin snapshot'ı alınır ve sonraki dış mutation görünmez")
        void graphTakesItsSnapshotAtConstructionTime() {
            // Arrange
            Profile ali = new Profile("1", "Ali", "ali@acme.com", "Acme");
            Profile ayse = new Profile("2", "Ayşe", "ayse@acme.com", "Acme");
            Profile mehmet = new Profile("3", "Mehmet", "mehmet@globex.com", "Globex");
            Map<String, Profile> profiles = new HashMap<>();
            profiles.put(ali.id(), ali);
            profiles.put(ayse.id(), ayse);
            List<String> friendIds = new ArrayList<>(List.of(ayse.id()));
            Map<String, List<String>> friends = new HashMap<>();
            friends.put(ali.id(), friendIds);
            SocialGraph graph = new SocialGraph(profiles, friends, Map.of());
            ProfileIterator iterator = new Facebook(graph).createFriendsIterator(ali.id());

            // Act
            profiles.put(mehmet.id(), mehmet);
            friendIds.add(mehmet.id());
            friends.put("unknown", List.of(mehmet.id()));
            List<String> recipients = new SocialSpammer().send(iterator, "snapshot");

            // Assert
            assertEquals(List.of("ayse@acme.com"), recipients);
        }

        @Test
        @DisplayName("null relation type ilk traversal'a kadar gizlenmeden constructor'da reddedilir")
        void nullRelationTypeFailsFast() {
            // Arrange
            SocialGraph graph = new SocialGraph(Map.of(), Map.of(), Map.of());

            // Act
            NullPointerException error = assertThrows(
                    NullPointerException.class,
                    () -> new SocialGraphIterator(graph, "1", null)
            );

            // Assert
            assertEquals("relationType cannot be null", error.getMessage());
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
