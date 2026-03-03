package ru.otus.spring.domain;

import java.util.List;
import java.util.stream.Collectors;

public record Question(String text, List<Answer> answers) {
    @Override
    public String toString() {
        return text + "\n" + answers.stream().map(Answer::toString).collect(Collectors.joining("\n"));
    }
}
