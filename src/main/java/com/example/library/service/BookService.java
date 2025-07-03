package com.example.library.service;

import com.example.library.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final List<Book> books = new ArrayList<>();

    public List<Book> getAllBooks() {
        return books;
    }

    public Book getBookById(Long id) {
        return books
                .stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Book addBook(Book book) {
        books.add(book);
        return book;
    }

    public boolean deleteBookById(Long id) {
        return books.removeIf(book -> book.getId().equals(id));
    }

    public Book updateBook(Book book, Long id) {
        if (id == null) {
            return null;
        }
        if (book.getId() == null) {
            return null;
        }
        if (!book.getId().equals(id)) {
            return null;
        }

        Book oldBook = getBookById(id);
        if (oldBook == null) {
            return null;
        }

        oldBook.setTitle(book.getTitle());
        oldBook.setAuthor(book.getAuthor());
        return oldBook;
    }
}
