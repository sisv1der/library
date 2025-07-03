package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final List<Book> books = new ArrayList<>();

    public List<BookDTO> getAllBooks() {

        return books.stream()
                .map(BookMapper::toBookDTO)
                .toList();
    }

    public BookDTO getDtoById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .map(BookMapper::toBookDTO)
                .orElse(null);
    }

    public Book getBookById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public BookDTO addBook(BookDTO book) {
        Long bookId = 100L;
        books.add(new Book(bookId, book.getTitle(), book.getAuthor()));
        return book;
    }

    public boolean deleteBookById(Long id) {
        return books.removeIf(book -> book.getId().equals(id));
    }

    public BookDTO updateBook(BookDTO book, Long id) {
        if (id == null) {
            return null;
        }

        Book oldBook = getBookById(id);
        if (oldBook == null) {
            return null;
        }

        oldBook.setTitle(book.getTitle());
        oldBook.setAuthor(book.getAuthor());
        return BookMapper.toBookDTO(oldBook);
    }

    public BookDTO patchBook(BookDTO book, Long id) {
        if (id == null) {
            return null;
        }
        Book oldBook = getBookById(id);
        if (oldBook == null) {
            return null;
        }
        oldBook.setTitle(book.getTitle() != null ? book.getTitle() : oldBook.getTitle());
        oldBook.setAuthor(book.getAuthor() != null ? book.getAuthor() : oldBook.getAuthor());

        return BookMapper.toBookDTO(oldBook);
    }
}
