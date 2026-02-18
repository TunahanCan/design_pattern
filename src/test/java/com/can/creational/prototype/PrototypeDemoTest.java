package com.can.creational.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PrototypeDemoTest {

    @Nested
    class CloneBehavior {

        // Bu test, clone edilen nesnenin deep copy ile üretildiğini doğrular.
        @Test
        void shouldCreateDeepCopyFromTemplate() {
            CandidateProfile template = new CandidateProfile(
                    "Template Candidate",
                    "Java Developer",
                    "Base summary",
                    new Address("Istanbul", "TR"),
                    List.of("Java", "Spring")
            );

            CandidateProfile cloned = template.copy()
                    .personalize("Ahmet Yılmaz", "Personal summary")
                    .addSkill("Kafka")
                    .relocateTo("Ankara", "TR");

            assertNotSame(template, cloned);
            assertNotSame(template.address(), cloned.address());
            assertNotSame(template.skills(), cloned.skills());

            assertEquals("Template Candidate", template.fullName());
            assertEquals("Istanbul", template.address().city());
            assertEquals(List.of("Java", "Spring"), template.skills());

            assertEquals("Ahmet Yılmaz", cloned.fullName());
            assertEquals("Ankara", cloned.address().city());
            assertEquals(List.of("Java", "Spring", "Kafka"), cloned.skills());
        }
    }

    @Nested
    class ExportFormatting {

        // Bu test, profil çıktısının beklenen formatta üretildiğini doğrular.
        @Test
        void shouldExportCardWithExpectedFormat() {
            CandidateProfile profile = new CandidateProfile(
                    "Elif Kaya",
                    "Java Developer",
                    "Cloud-native projects.",
                    new Address("Izmir", "TR"),
                    List.of("Java", "Docker")
            );

            String card = profile.exportCard();

            assertTrue(card.contains("[PROFILE] Name:Elif Kaya"));
            assertTrue(card.contains("Role:Java Developer"));
            assertTrue(card.contains("Location:Izmir/TR"));
            assertTrue(card.contains("Skills:Java, Docker"));
        }
    }

    @Nested
    class RegistryFlow {

        // Bu test, registry'nin doğru template'i klonlayıp döndürdüğünü doğrular.
        @Test
        void shouldCloneProfileFromRegistry() {
            CandidateProfileRegistry registry = new CandidateProfileRegistry();
            registry.register("java-default", new CandidateProfile(
                    "Template Candidate",
                    "Java Developer",
                    "Base summary",
                    new Address("Istanbul", "TR"),
                    List.of("Java", "Spring")
            ));

            CandidateProfile cloned = registry.cloneOf("java-default")
                    .personalize("Can Demir", "Backend engineer")
                    .addSkill("PostgreSQL");

            assertEquals("Can Demir", cloned.fullName());
            assertEquals(List.of("Java", "Spring", "PostgreSQL"), cloned.skills());
        }

        // Bu test, registry'de olmayan template için anlamlı hata fırlatıldığını doğrular.
        @Test
        void shouldThrowWhenTemplateIsMissing() {
            CandidateProfileRegistry registry = new CandidateProfileRegistry();

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> registry.cloneOf("missing-template"));

            assertEquals("No template found for id: missing-template", error.getMessage());
        }
    }
}
