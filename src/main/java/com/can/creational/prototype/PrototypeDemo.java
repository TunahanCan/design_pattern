package com.can.creational.prototype;

public class PrototypeDemo {

    interface Prototype<T> {
        T copy();
    }

    static class Resume implements Prototype<Resume> {
        private final String fullName;
        private final String position;
        private final String template;

        Resume(String fullName, String position, String template) {
            this.fullName = fullName;
            this.position = position;
            this.template = template;
        }

        private Resume(Resume source) {
            this.fullName = source.fullName;
            this.position = source.position;
            this.template = source.template;
        }

        Resume withName(String name) {
            return new Resume(name, this.position, this.template);
        }

        @Override
        public Resume copy() {
            return new Resume(this);
        }

        @Override
        public String toString() {
            return "Resume{" +
                    "fullName='" + fullName + '\'' +
                    ", position='" + position + '\'' +
                    ", template='" + template + '\'' +
                    '}';
        }
    }

    public static void run() {
        System.out.println("4) Prototype");

        Resume baseTemplate = new Resume("Şablon Aday", "Java Developer", "Minimal CV V1");
        Resume ahmetResume = baseTemplate.copy().withName("Ahmet Yılmaz");
        Resume elifResume = baseTemplate.copy().withName("Elif Kaya");

        System.out.println("Template: " + baseTemplate);
        System.out.println("Clone-1: " + ahmetResume);
        System.out.println("Clone-2: " + elifResume);
        System.out.println();
    }
}
