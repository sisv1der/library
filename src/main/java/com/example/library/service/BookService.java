package com.example.library.service;

import com.example.library.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private List<Book> books;

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

    public void deleteBookById(Long id) {
        books.removeIf(book -> book.getId().equals(id));
    }
}
