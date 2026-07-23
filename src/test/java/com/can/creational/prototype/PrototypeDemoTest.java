package com.can.creational.prototype;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Prototype — template kopyalanır, clone bağımsız biçimde özelleştirilir")
class PrototypeDemoTest {

    @Nested
    @DisplayName("Copy semantiği")
    class CopySemantics {

        @Test
        @DisplayName("copy bütün değerleri yeni bir profile taşır")
        void copyPreservesAllValuesInANewProfile() {
            // Arrange
            CandidateProfile template = javaTemplate();

            // Act
            CandidateProfile clone = template.copy();

            // Assert
            assertAll(
                    () -> assertNotSame(template, clone),
                    () -> assertEquals(template.fullName(), clone.fullName()),
                    () -> assertEquals(template.targetRole(), clone.targetRole()),
                    () -> assertEquals(template.summary(), clone.summary()),
                    () -> assertEquals(template.address().city(), clone.address().city()),
                    () -> assertEquals(template.address().country(), clone.address().country()),
                    () -> assertEquals(template.skills(), clone.skills())
            );
        }

        @Test
        @DisplayName("Clone üzerinde yapılan değişiklik template'e sızmaz")
        void cloneCustomizationDoesNotLeakIntoTheTemplate() {
            // Arrange
            CandidateProfile template = javaTemplate();

            // Act
            CandidateProfile clone = template.copy()
                    .personalize("Ahmet Yılmaz", "Personal summary")
                    .addSkill("Kafka")
                    .relocateTo("Ankara", "TR");

            // Assert
            assertAll(
                    () -> assertEquals("Template Candidate", template.fullName()),
                    () -> assertEquals("Base summary", template.summary()),
                    () -> assertEquals("Istanbul", template.address().city()),
                    () -> assertEquals(List.of("Java", "Spring"), template.skills()),
                    () -> assertEquals("Ahmet Yılmaz", clone.fullName()),
                    () -> assertEquals("Personal summary", clone.summary()),
                    () -> assertEquals("Ankara", clone.address().city()),
                    () -> assertEquals(List.of("Java", "Spring", "Kafka"), clone.skills())
            );
        }
    }

    @Nested
    @DisplayName("Public constructor da defensive copy uygular")
    class ConstructorIsolation {

        @Test
        @DisplayName("Constructor girdileri sonradan değişse bile profil ilk snapshot'ı korur")
        void profileDoesNotShareConstructorInputs() {
            // Arrange
            Address sourceAddress = new Address("Istanbul", "TR");
            List<String> sourceSkills = new ArrayList<>(List.of("Java"));
            CandidateProfile profile = new CandidateProfile(
                    "Can Demir",
                    "Java Developer",
                    "Backend engineer",
                    sourceAddress,
                    sourceSkills
            );

            // Act
            sourceAddress.moveTo("Berlin", "DE");
            sourceSkills.add("Injected");

            // Assert
            assertAll(
                    () -> assertEquals("Istanbul", profile.address().city()),
                    () -> assertEquals("TR", profile.address().country()),
                    () -> assertEquals(List.of("Java"), profile.skills())
            );
        }

        @Test
        @DisplayName("Getter'lar mutable iç alanları dışarı açmaz")
        void accessorsDoNotExposeMutableInternalState() {
            // Arrange
            CandidateProfile profile = javaTemplate();
            Address detachedAddress = profile.address();

            // Act
            detachedAddress.moveTo("Berlin", "DE");

            // Assert
            assertAll(
                    () -> assertEquals("Istanbul", profile.address().city()),
                    () -> assertEquals("TR", profile.address().country()),
                    () -> assertThrows(
                            UnsupportedOperationException.class,
                            () -> profile.skills().add("Injected")
                    ),
                    () -> assertEquals(List.of("Java", "Spring"), profile.skills())
            );
        }
    }

    @Nested
    @DisplayName("Registry template'ten her çağrıda yeni clone üretir")
    class RegistryContract {

        @Test
        @DisplayName("Aynı ID ile iki istek birbirinden bağımsız clone'lar döndürür")
        void eachRegistryLookupReturnsAnIndependentClone() {
            // Arrange
            CandidateProfileRegistry registry = new CandidateProfileRegistry();
            registry.register("java-default", javaTemplate());

            // Act
            CandidateProfile first = registry.cloneOf("java-default").addSkill("Kafka");
            CandidateProfile second = registry.cloneOf("java-default").relocateTo("Izmir", "TR");

            // Assert
            assertAll(
                    () -> assertNotSame(first, second),
                    () -> assertEquals(List.of("Java", "Spring", "Kafka"), first.skills()),
                    () -> assertEquals(List.of("Java", "Spring"), second.skills()),
                    () -> assertEquals("Istanbul", first.address().city()),
                    () -> assertEquals("Izmir", second.address().city())
            );
        }

        @Test
        @DisplayName("register canlı referans yerine template snapshot'ı saklar")
        void registryStoresASnapshotOfTheRegisteredTemplate() {
            // Arrange
            CandidateProfile source = javaTemplate();
            CandidateProfileRegistry registry = new CandidateProfileRegistry();
            registry.register("java-default", source);

            // Act
            source.addSkill("Injected Later").relocateTo("Berlin", "DE");
            CandidateProfile clone = registry.cloneOf("java-default");

            // Assert
            assertAll(
                    () -> assertEquals(List.of("Java", "Spring"), clone.skills()),
                    () -> assertEquals("Istanbul", clone.address().city()),
                    () -> assertEquals("TR", clone.address().country())
            );
        }

        @Test
        @DisplayName("Kayıtlı olmayan template ID'si açık hata mesajı üretir")
        void missingTemplateProducesAnExplicitError() {
            // Arrange
            CandidateProfileRegistry registry = new CandidateProfileRegistry();

            // Act
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class,
                    () -> registry.cloneOf("missing-template")
            );

            // Assert
            assertEquals("No template found for id: missing-template", error.getMessage());
        }
    }

    @Nested
    @DisplayName("Domain sınırı normalize ve doğrulama uygular")
    class ValidationBoundary {

        @Test
        @DisplayName("Profil, adres, skill ve template ID çevre boşluklarını temizler")
        void textInputsAreNormalizedAtTheirOwningBoundary() {
            // Arrange
            CandidateProfile profile = new CandidateProfile(
                    "  Can Demir ",
                    " Java Developer ",
                    " Backend engineer ",
                    new Address(" Istanbul ", " TR "),
                    List.of(" Java ", " Spring ")
            );
            CandidateProfileRegistry registry = new CandidateProfileRegistry();

            // Act
            registry.register(" java-default ", profile);
            CandidateProfile clone = registry.cloneOf("java-default")
                    .addSkill(" Kafka ")
                    .relocateTo(" Ankara ", " TR ");

            // Assert
            assertAll(
                    () -> assertEquals("Can Demir", clone.fullName()),
                    () -> assertEquals("Java Developer", clone.targetRole()),
                    () -> assertEquals("Backend engineer", clone.summary()),
                    () -> assertEquals("Ankara", clone.address().city()),
                    () -> assertEquals(List.of("Java", "Spring", "Kafka"), clone.skills())
            );
        }

        @Test
        @DisplayName("Blank domain değeri nesneyi yarım kurmadan reddedilir")
        void blankDomainValuesAreRejected() {
            // Arrange & Act
            IllegalArgumentException cityError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Address(" ", "TR")
            );
            IllegalArgumentException skillError = assertThrows(
                    IllegalArgumentException.class,
                    () -> javaTemplate().addSkill(" ")
            );
            IllegalArgumentException templateIdError = assertThrows(
                    IllegalArgumentException.class,
                    () -> new CandidateProfileRegistry().register(" ", javaTemplate())
            );

            // Assert
            assertAll(
                    () -> assertEquals("city cannot be blank", cityError.getMessage()),
                    () -> assertEquals("skill cannot be blank", skillError.getMessage()),
                    () -> assertEquals("templateId cannot be blank", templateIdError.getMessage())
            );
        }

        @Test
        @DisplayName("Çok alanlı mutation bütün değerler doğrulanmadan state'i değiştirmez")
        void failedMultiFieldMutationLeavesTheProfileUnchanged() {
            // Arrange
            CandidateProfile profile = javaTemplate();

            // Act
            IllegalArgumentException summaryError = assertThrows(
                    IllegalArgumentException.class,
                    () -> profile.personalize("New Name", " ")
            );
            IllegalArgumentException countryError = assertThrows(
                    IllegalArgumentException.class,
                    () -> profile.relocateTo("Ankara", " ")
            );

            // Assert
            assertAll(
                    () -> assertEquals("summary cannot be blank", summaryError.getMessage()),
                    () -> assertEquals("country cannot be blank", countryError.getMessage()),
                    () -> assertEquals("Template Candidate", profile.fullName()),
                    () -> assertEquals("Base summary", profile.summary()),
                    () -> assertEquals("Istanbul", profile.address().city()),
                    () -> assertEquals("TR", profile.address().country())
            );
        }
    }

    @Nested
    @DisplayName("Dışa aktarılan kart clone'un güncel durumunu anlatır")
    class ExportFormatting {

        @Test
        @DisplayName("exportCard bütün alanları kararlı sırada birleştirir")
        void exportCardContainsTheCompleteProfileState() {
            // Arrange
            CandidateProfile profile = new CandidateProfile(
                    "Elif Kaya",
                    "Java Developer",
                    "Cloud-native projects.",
                    new Address("Izmir", "TR"),
                    List.of("Java", "Docker")
            );

            // Act
            String card = profile.exportCard();

            // Assert
            assertEquals(
                    "[PROFILE] Name:Elif Kaya | Role:Java Developer | Location:Izmir/TR"
                            + " | Skills:Java, Docker | Summary:Cloud-native projects.",
                    card
            );
        }
    }

    private static CandidateProfile javaTemplate() {
        return new CandidateProfile(
                "Template Candidate",
                "Java Developer",
                "Base summary",
                new Address("Istanbul", "TR"),
                List.of("Java", "Spring")
        );
    }
}
