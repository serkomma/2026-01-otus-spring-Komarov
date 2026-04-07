package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (var resource = getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName())) {
            if (resource == null) {
                throw new QuestionReadException("Resource not found: " + fileNameProvider.getTestFileName());
            }
            try (Reader reader = new BufferedReader(new InputStreamReader(resource))) {
                CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                        .withType(QuestionDto.class)
                        .withSeparator(';')
                        .withSkipLines(1)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                return csvToBean.parse().stream().map(QuestionDto::toDomainObject).toList();

            }
        } catch (IOException e) {
            throw new QuestionReadException("IO Error occurred", e);
        }
    }
}
