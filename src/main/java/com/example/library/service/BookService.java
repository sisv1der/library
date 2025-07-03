package com.example.library.service;

import com.example.library.model.Book;
import org.springframework.stereotype.Service;

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
}
