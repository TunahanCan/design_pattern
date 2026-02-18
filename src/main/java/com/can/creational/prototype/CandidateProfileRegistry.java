package com.can.creational.prototype;

import java.util.HashMap;
import java.util.Map;

public class CandidateProfileRegistry {
    private final Map<String, CandidateProfile> templates = new HashMap<>();

    public void register(String templateId, CandidateProfile profile) {
        templates.put(templateId, profile);
    }

    public CandidateProfile cloneOf(String templateId) {
        CandidateProfile template = templates.get(templateId);

        if (template == null) {
            throw new IllegalArgumentException("No template found for id: " + templateId);
        }

        return template.copy();
    }
}
