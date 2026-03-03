package ru.otus.spring.domain;

public record Answer(String text, boolean isCorrect) {
    @Override
    public String toString() {
        return text() + " (" + isCorrect() + ")";
    }
}
