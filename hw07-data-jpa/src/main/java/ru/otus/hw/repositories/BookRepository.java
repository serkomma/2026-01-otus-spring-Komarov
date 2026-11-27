package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Book;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findById(@Nonnull Long id);

    void deleteById(long id);
}
