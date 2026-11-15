package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book insert(String title, long authorId, long genreId) {
        return save(0, title, authorId, genreId);
    }

    @Override
    public Book update(long id, String title, long authorId, long genreId) {
        return save(id, title, authorId, genreId);
    }

    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public Book save(long id, String title, long authorId, long genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        Hibernate.initialize(author);
        Hibernate.initialize(genre);
        var book = new Book(id, title, author, genre, Collections.emptyList());
        bookRepository.save(book);
        return book;
    }
}
