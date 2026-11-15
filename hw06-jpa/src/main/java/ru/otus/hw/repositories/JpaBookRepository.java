package ru.otus.hw.repositories;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@Transactional
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaBookRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<Book> graph = em.createEntityGraph(Book.class);
        graph.addAttributeNodes("author");
        graph.addAttributeNodes("genre");
        Map<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.fetchgraph", graph);
        return Optional.ofNullable(em.find(Book.class, id, hints));
    }

    @Override
    public List<Book> findAll() {
//        return jdbc.query(
//                "select b.id as id, b.title as title, a.id as author_id, a.full_name as author_full_name, " +
//                        "g.id as genre_id, g.name as genre_name " +
//                        "from books as b " +
//                        "join authors as a on a.id = b.author_id " +
//                        "join genres as g on g.id = b.genre_id", new JpaBookRepository.BookRowMapper()
//        );
        EntityGraph<?> entityGraph = em.getEntityGraph("book-comments-entity-graph");
        TypedQuery<Book> query = em.createQuery("select distinct b from Book b join fetch b.author a join fetch b.genre g", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("delete " +
                "from Book b " +
                "where b.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

//    private Book insert(Book book) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("title", book.getTitle());
//        params.addValue("author_id", book.getAuthor().getId());
//        params.addValue("genre_id", book.getGenre().getId());
//        var keyHolder = new GeneratedKeyHolder();
//
//        jdbc.update("insert into books (title, author_id, genre_id) values (:title, :author_id, :genre_id)", params, keyHolder);
//
//        book.setId(keyHolder.getKeyAs(Long.class));
//        return book;
//    }
//
//    private Book update(Book book) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("id", book.getId());
//        params.addValue("title", book.getTitle());
//        params.addValue("author_id", book.getAuthor().getId());
//        params.addValue("genre_id", book.getGenre().getId());
//        int count = jdbc.update("update books set title = :title, author_id = :author_id, genre_id = :genre_id " +
//                "where id = :id", params);
//        if (count == 0) {
//            throw new EntityNotFoundException("Не обновлено ни одной записи в БД");
//        }
//        return book;
//    }
}
