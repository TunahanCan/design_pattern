package com.can.creational.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CandidateProfile implements Prototype<CandidateProfile> {
    private String fullName;
    private final String targetRole;
    private String summary;
    private final Address address;
    private final List<String> skills;

    public CandidateProfile(String fullName, String targetRole, String summary, Address address, List<String> skills) {
        this.fullName = normalize(fullName, "fullName");
        this.targetRole = normalize(targetRole, "targetRole");
        this.summary = normalize(summary, "summary");
        this.address = new Address(Objects.requireNonNull(address, "address cannot be null"));
        this.skills = normalizeSkills(skills);
    }

    private CandidateProfile(CandidateProfile source) {
        this.fullName = source.fullName;
        this.targetRole = source.targetRole;
        this.summary = source.summary;
        this.address = new Address(source.address);
        this.skills = new ArrayList<>(source.skills);
    }

    @Override
    public CandidateProfile copy() {
        return new CandidateProfile(this);
    }

    public CandidateProfile personalize(String fullName, String summary) {
        String normalizedFullName = normalize(fullName, "fullName");
        String normalizedSummary = normalize(summary, "summary");
        this.fullName = normalizedFullName;
        this.summary = normalizedSummary;
        return this;
    }

    public CandidateProfile addSkill(String skill) {
        this.skills.add(normalize(skill, "skill"));
        return this;
    }

    public CandidateProfile relocateTo(String city, String country) {
        this.address.moveTo(city, country);
        return this;
    }

    public String fullName() {
        return fullName;
    }

    public String targetRole() {
        return targetRole;
    }

    public String summary() {
        return summary;
    }

    public Address address() {
        return new Address(address);
    }

    public List<String> skills() {
        return List.copyOf(skills);
    }

    public String exportCard() {
        return "[PROFILE] Name:" + fullName +
                " | Role:" + targetRole +
                " | Location:" + address +
                " | Skills:" + String.join(", ", skills) +
                " | Summary:" + summary;
    }

    private static ArrayList<String> normalizeSkills(List<String> skills) {
        Objects.requireNonNull(skills, "skills cannot be null");
        ArrayList<String> normalizedSkills = new ArrayList<>(skills.size());
        skills.forEach(skill -> normalizedSkills.add(normalize(skill, "skill")));
        return normalizedSkills;
    }

    private static String normalize(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " cannot be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }
}
