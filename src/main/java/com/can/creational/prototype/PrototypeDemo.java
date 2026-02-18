package com.can.creational.prototype;

import java.util.List;

public class PrototypeDemo {

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

        CandidateProfile ahmet = registry.cloneOf("java-default")
                .personalize("Ahmet Yılmaz", "Backend-focused developer with 5 years of experience.")
                .addSkill("Kafka")
                .relocateTo("Ankara", "TR");

        CandidateProfile elif = registry.cloneOf("java-default")
                .personalize("Elif Kaya", "Cloud-native projects and microservice architecture.")
                .addSkill("Docker");

        System.out.println("Template : " + javaTemplate.exportCard());
        System.out.println("Clone-1  : " + ahmet.exportCard());
        System.out.println("Clone-2  : " + elif.exportCard());
        System.out.println();
    }
}
