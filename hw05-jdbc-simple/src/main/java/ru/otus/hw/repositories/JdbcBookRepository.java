package ru.otus.hw.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbc;

    public JdbcBookRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations;
    }

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "select b.id as id, b.title as title, a.id as author_id, a.full_name as author_full_name, " +
                                    "g.id as genre_id, g.name as genre_name " +
                                    "from books as b " +
                                    "join authors as a on a.id = b.author_id " +
                                    "join genres as g on g.id = b.genre_id " +
                                    "where b.id = :id", params, new JdbcBookRepository.BookRowMapper()
                    )
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(
                "select b.id as id, b.title as title, a.id as author_id, a.full_name as author_full_name, " +
                        "g.id as genre_id, g.name as genre_name " +
                        "from books as b " +
                        "join authors as a on a.id = b.author_id " +
                        "join genres as g on g.id = b.genre_id", new JdbcBookRepository.BookRowMapper()
        );
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        jdbc.update(
                "delete from books where id = :id", params
        );
    }

    private Book insert(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        var keyHolder = new GeneratedKeyHolder();

        jdbc.update("insert into books (title, author_id, genre_id) values (:title, :author_id, :genre_id)", params, keyHolder);

        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        int count = jdbc.update("update books set title = :title, author_id = :author_id, genre_id = :genre_id " +
                "where id = :id", params);
        if (count == 0) {
            throw new EntityNotFoundException("Не обновлено ни одной записи в БД");
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("id");
            String bookTitle = rs.getString("title");
            long authorId = rs.getLong("author_id");
            String authorFullName = rs.getString("author_full_name");
            Author author = new Author(authorId, authorFullName);
            long genreId = rs.getLong("genre_id");
            String genreName = rs.getString("genre_name");
            Genre genre = new Genre(genreId, genreName);
            return new Book(bookId, bookTitle, author, genre);
        }
    }
}
