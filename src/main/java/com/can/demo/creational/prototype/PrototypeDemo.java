package com.can.demo.creational.prototype;

import java.util.List;

import com.can.creational.prototype.Address;
import com.can.creational.prototype.CandidateProfile;
import com.can.creational.prototype.CandidateProfileRegistry;

public final class PrototypeDemo {

    private PrototypeDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("4) Prototype");

        CandidateProfileRegistry registry = new CandidateProfileRegistry();

        CandidateProfile javaTemplate = new CandidateProfile(
                "Template Candidate",
                "Java Developer",
                "Ready to adapt fast.",
                new Address("Istanbul", "TR"),
                List.of("Java", "Spring Boot", "SQL")
        );

        registry.register("java-default", javaTemplate);

        System.out.println("Temel örnek — doğrudan deep copy:");
        CandidateProfile ahmet = javaTemplate.copy()
                .personalize(
                        "Ahmet Yılmaz",
                        "Backend-focused developer with 5 years of experience."
                )
                .addSkill("Kafka")
                .relocateTo("Ankara", "TR");
        System.out.println("Direct Copy : " + ahmet.exportCard());

        System.out.println(
                "Daha gerçekçi örnek — registry'den bağımsız çalışma kopyası:"
        );
        CandidateProfile elif = registry.cloneOf("java-default")
                .personalize("Elif Kaya", "Cloud-native projects and microservice architecture.")
                .addSkill("Docker");
        System.out.println("Registry Copy: " + elif.exportCard());

        System.out.println("Template : " + javaTemplate.exportCard());
        System.out.println();
    }
}
