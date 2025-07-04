package org.example.dataprotal.enums;

public enum Language {
    AZE("Azərbaycan dili"), EN("English"), RU("Русский");

    String fullName;

    Language(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
