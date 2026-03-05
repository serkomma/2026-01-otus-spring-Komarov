import org.junit.jupiter.api.Test;
import ru.otus.spring.config.AppProperties;
import ru.otus.spring.dao.CsvQuestionDao;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.exceptions.QuestionReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvPrinterTest {

    private QuestionDao questionDao;

    void setUp(String filename) {
        questionDao = new CsvQuestionDao(new AppProperties(filename));
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
}
