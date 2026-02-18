package com.can.creational.builder;

import java.util.List;

public class BuilderDemo {

    static class Report {
        private final String title;
        private final String summary;
        private final List<String> sections;
        private final boolean includeChart;

        private Report(Builder builder) {
            this.title = builder.title;
            this.summary = builder.summary;
            this.sections = List.copyOf(builder.sections);
            this.includeChart = builder.includeChart;
        }

        @Override
        public String toString() {
            return "Report{" +
                    "title='" + title + '\'' +
                    ", summary='" + summary + '\'' +
                    ", sections=" + sections +
                    ", includeChart=" + includeChart +
                    '}';
        }

        static class Builder {
            private String title;
            private String summary;
            private List<String> sections = List.of();
            private boolean includeChart;

            Builder title(String title) {
                this.title = title;
                return this;
            }

            Builder summary(String summary) {
                this.summary = summary;
                return this;
            }

            Builder sections(List<String> sections) {
                this.sections = sections;
                return this;
            }

            Builder includeChart(boolean includeChart) {
                this.includeChart = includeChart;
                return this;
            }

            Report build() {
                return new Report(this);
            }
        }
    }

    public static void run() {
        System.out.println("3) Builder");

        Report report = new Report.Builder()
                .title("Q1 Satış Raporu")
                .summary("İlk çeyrek satış performansı")
                .sections(List.of("Özet", "Bölgesel Dağılım", "Riskler"))
                .includeChart(true)
                .build();

        System.out.println(report);
        System.out.println();
    }
}
