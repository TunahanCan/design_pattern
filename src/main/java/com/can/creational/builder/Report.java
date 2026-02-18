package com.can.creational.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class Report {
    private final String title;
    private final String summary;
    private final List<String> sections;
    private final boolean includeChart;
    private final String author;

    private Report(Builder builder) {
        this.title = builder.title;
        this.summary = builder.summary;
        this.sections = List.copyOf(builder.sections);
        this.includeChart = builder.includeChart;
        this.author = builder.author;
    }

    public String title() {
        return title;
    }

    public String summary() {
        return summary;
    }

    public List<String> sections() {
        return sections;
    }

    public boolean includeChart() {
        return includeChart;
    }

    public String author() {
        return author;
    }

    public String exportCard() {
        String sectionDisplay = sections.isEmpty() ? "No sections" : String.join(", ", sections);
        return new StringJoiner(" | ", "[REPORT] ", "")
                .add("Title:" + title)
                .add("Author:" + author)
                .add("Summary:" + summary)
                .add("Sections:" + sectionDisplay)
                .add("Chart:" + (includeChart ? "YES" : "NO"))
                .toString();
    }

    public static Builder builder(String title) {
        return new Builder(title);
    }

    @Override
    public String toString() {
        return "Report{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", sections=" + sections +
                ", includeChart=" + includeChart +
                ", author='" + author + '\'' +
                '}';
    }

    public static final class Builder {
        private final String title;
        private String summary = "No summary";
        private List<String> sections = List.of();
        private boolean includeChart;
        private String author = "Unknown";

        private Builder(String title) {
            this.title = normalize(title, "title");
        }

        public Builder summary(String summary) {
            this.summary = normalize(summary, "summary");
            return this;
        }

        public Builder sections(List<String> sections) {
            Objects.requireNonNull(sections, "sections cannot be null");
            this.sections = new ArrayList<>(sections);
            return this;
        }

        public Builder addSection(String section) {
            String normalized = normalize(section, "section");
            List<String> mutable = new ArrayList<>(this.sections);
            mutable.add(normalized);
            this.sections = mutable;
            return this;
        }

        public Builder includeChart(boolean includeChart) {
            this.includeChart = includeChart;
            return this;
        }

        public Builder author(String author) {
            this.author = normalize(author, "author");
            return this;
        }

        public Report build() {
            return new Report(this);
        }

        private static String normalize(String value, String fieldName) {
            String normalized = Objects.requireNonNull(value, fieldName + " cannot be null").trim();
            if (normalized.isBlank()) {
                throw new IllegalArgumentException(fieldName + " cannot be blank");
            }
            return normalized;
        }
    }
}
