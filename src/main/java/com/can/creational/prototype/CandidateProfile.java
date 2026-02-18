package com.can.creational.prototype;

import java.util.ArrayList;
import java.util.List;

public class CandidateProfile implements Prototype<CandidateProfile> {
    private String fullName;
    private final String targetRole;
    private String summary;
    private final Address address;
    private final List<String> skills;

    public CandidateProfile(String fullName, String targetRole, String summary, Address address, List<String> skills) {
        this.fullName = fullName;
        this.targetRole = targetRole;
        this.summary = summary;
        this.address = new Address(address);
        this.skills = new ArrayList<>(skills);
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
        this.fullName = fullName;
        this.summary = summary;
        return this;
    }

    public CandidateProfile addSkill(String skill) {
        this.skills.add(skill);
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
        return address;
    }

    public List<String> skills() {
        return skills;
    }

    public String exportCard() {
        return "[PROFILE] Name:" + fullName +
                " | Role:" + targetRole +
                " | Location:" + address +
                " | Skills:" + String.join(", ", skills) +
                " | Summary:" + summary;
    }
}
