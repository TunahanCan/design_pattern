package com.can.creational.prototype;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CandidateProfileRegistry {
    private final Map<String, CandidateProfile> templates = new HashMap<>();

    public void register(String templateId, CandidateProfile profile) {
        templates.put(
                normalizeTemplateId(templateId),
                Objects.requireNonNull(profile, "profile cannot be null").copy()
        );
    }

    public CandidateProfile cloneOf(String templateId) {
        String normalizedTemplateId = normalizeTemplateId(templateId);
        CandidateProfile template = templates.get(normalizedTemplateId);

        if (template == null) {
            throw new IllegalArgumentException("No template found for id: " + normalizedTemplateId);
        }

        return template.copy();
    }

    private static String normalizeTemplateId(String templateId) {
        String normalized = Objects.requireNonNull(
                templateId,
                "templateId cannot be null"
        ).trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("templateId cannot be blank");
        }
        return normalized;
    }
}
