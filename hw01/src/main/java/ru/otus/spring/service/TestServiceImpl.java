package ru.otus.spring.service;

import lombok.RequiredArgsConstructor;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        questions.forEach(it -> {
                    ioService.printFormattedLine(it.text());
                    ioService.printFormattedLine(printAnswers(it.answers()));
                }
        );
    }

    String printAnswers(Collection<Answer> answers) {
        return answers
                .stream()
                .map(answer -> answer.text() + " (" + answer.isCorrect() + ")")
                .collect(Collectors.joining("\n"));
    }
}
