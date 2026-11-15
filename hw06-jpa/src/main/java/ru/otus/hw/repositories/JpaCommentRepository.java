package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

@Repository
@Transactional
public class JpaCommentRepository implements CommentRepository{
    private final EntityManager em;

    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Comment> findByBook(Book book) {
        TypedQuery<Comment> query = em.createQuery("select c " +
                        "from Comment c " +
                        "where c.book = :book",
                Comment.class);
        query.setParameter("book", book);
        return query.getResultList();
    }
}
