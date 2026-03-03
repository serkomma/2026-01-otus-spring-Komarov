import org.junit.jupiter.api.Test;
import ru.otus.spring.config.AppProperties;
import ru.otus.spring.dao.CsvQuestionDao;
import ru.otus.spring.dao.QuestionDao;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvPrinterTest {
    QuestionDao questionDao = new CsvQuestionDao(new AppProperties("testQuestions.csv"));

    @Test
    public void printTest(){
        var result = questionDao.findAll();
        assertEquals("Wanna some tests?", result.get(0).text());
        assertEquals(3, result.get(0).answers().size());
    }
}
