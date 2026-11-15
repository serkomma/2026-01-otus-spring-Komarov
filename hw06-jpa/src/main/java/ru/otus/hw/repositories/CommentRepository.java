package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByBook(Book book);
}
