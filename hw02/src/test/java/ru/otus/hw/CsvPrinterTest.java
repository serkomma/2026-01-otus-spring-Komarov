package ru.otus.hw;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.TestServiceImpl;
import ru.otus.hw.service.ResultServiceImpl;
import ru.otus.hw.service.StudentServiceImpl;
import ru.otus.hw.service.TestRunnerServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvPrinterTest {

    private QuestionDao questionDao;

    private IOService ioService;

    private StudentService studentService;

    private TestService testService;

    private ResultService resultService;

    private TestRunnerService testRunnerService;

    void setUp(String filename, InputStream inputStream, PrintStream printStream) {
        AppProperties appProperties = new AppProperties(1, filename);
        questionDao = new CsvQuestionDao(appProperties);
        ioService = new StreamsIOService(printStream, inputStream);
        studentService = new StudentServiceImpl(ioService);
        testService = new TestServiceImpl(ioService, questionDao);
        resultService = new ResultServiceImpl(appProperties, ioService);
        testRunnerService = new TestRunnerServiceImpl(testService, studentService, resultService);
    }

    void setUp(String filename) {
        setUp(filename, System.in, System.out);
    }

    @Test
    public void shouldReadAllQuestionsWhenFileIsCorrect() {
        setUp("testQuestions.csv");
        var result = questionDao.findAll();
        assertEquals("Wanna some tests?", result.get(0).text());
        assertEquals(3, result.get(0).answers().size());
    }

    @Test
    public void shouldThrowExceptionWhenFileIsNotFound() {
        setUp("testQuestionsTest.csv");
        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }

    @Test
    public void studentNameShouldBeWritten() {
        String resultLine = "Jerry" + System.lineSeparator() + "Smith" + System.lineSeparator();
        InputStream inputStream = new ByteArrayInputStream(resultLine.getBytes());
        setUp("testQuestions.csv", inputStream, System.out);
        var student = studentService.determineCurrentStudent();
        assertEquals("Jerry", student.firstName());
        assertEquals("Smith", student.lastName());
    }

    @Test
    public void testResultsShouldBeCountedCorrect() {
        Student student = new Student("Jerry", "Smith");
        TestResult testResult = new TestResult(student);
        Question question = new Question("Question?",
                List.of(new Answer("Right", true))
        );
        testResult.applyAnswer(
                question,
                true
        );
        assertEquals(1, testResult.getRightAnswersCount());
        testResult.applyAnswer(
                question,
                false
        );
        assertEquals(1, testResult.getRightAnswersCount());
        assertEquals(2, testResult.getAnsweredQuestions().size());
    }

    @Test
    void answersShouldBePrintedWithNumbers(){
        Question question = new Question("Question?",
                List.of(
                        new Answer("Right", true),
                        new Answer("No", true),
                        new Answer("Maybe", true)
                )
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        IOService ioService = new StreamsIOService(printStream, System.in);
        ioService.printNumberedList(question.answers().stream().map(Answer::text).toList());
        var result = outputStream.toString(StandardCharsets.UTF_8).lines().toList();
        IntStream.range(0, result.size()).forEach(i ->
                {
                    Integer firstSymbol = Integer.parseInt(result.get(i).substring(0, 1));
                    assertEquals(i + 1, firstSymbol);
                }
        );
    }

    @Test
    public void studentTestShouldBeCorrect() {
        String resultLine = "1";
        InputStream inputStream = new ByteArrayInputStream(resultLine.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        setUp("testQuestions.csv", inputStream, printStream);
        Student student = new Student("Jerry", "Smith");
        var testResult = testService.executeTestFor(student);
        assertEquals(1, testResult.getRightAnswersCount());
        resultService.showResult(testResult);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        String endLine = result.lines().skip(12).findFirst().orElse("");
        assertEquals("Congratulations! You passed test!", endLine);
    }

    @Test
    public void studentTestShouldNotBeCorrect() {
        String resultLine = "2";
        InputStream inputStream = new ByteArrayInputStream(resultLine.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        setUp("testQuestions.csv", inputStream, printStream);
        Student student = new Student("Jerry", "Smith");
        var testResult = testService.executeTestFor(student);
        assertEquals(0, testResult.getRightAnswersCount());
        resultService.showResult(testResult);
        String result = outputStream.toString(StandardCharsets.UTF_8);
        String endLine = result.lines().skip(12).findFirst().orElse("");
        assertEquals("Sorry. You fail test.", endLine);
    }
}