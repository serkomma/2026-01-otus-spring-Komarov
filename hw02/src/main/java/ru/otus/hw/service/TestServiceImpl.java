package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = false;
            ioService.printLine(question.text());
            ioService.printNumberedList(question.answers().stream().map(Answer::text).toList());
            // Не нашёл в условии, допустимы ли несколько правильных ответов, будем считать, что нет
            int rightAnswer = question.answers().stream().map(Answer::isCorrect).toList().indexOf(true);
            if (rightAnswer == -1) {
                throw new QuestionReadException("Для вопроса правильный ответ неизвестен");
            } else {
                rightAnswer += 1;
            }
            int studentAnswer = ioService.readIntForRange(0, question.answers().size(), "Неверный формат ответа");
            isAnswerValid = studentAnswer == rightAnswer;
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
