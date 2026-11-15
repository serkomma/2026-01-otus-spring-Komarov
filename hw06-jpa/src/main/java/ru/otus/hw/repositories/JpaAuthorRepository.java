package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaAuthorRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Author> findAll() {
        String jpql = "SELECT a FROM Author a";
        TypedQuery<Author> query = em.createQuery(jpql, Author.class);
        return query.getResultList();
    }

    @Override
    @Query("select a from Author as a where a.id = :id")
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }
}
