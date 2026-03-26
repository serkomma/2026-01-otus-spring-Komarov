package ru.otus.spring.dao;

import ru.otus.spring.domain.Question;
import ru.otus.spring.exceptions.QuestionReadException;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll() throws QuestionReadException;
}
