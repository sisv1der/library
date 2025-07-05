package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;
import com.example.library.model.BookPatchDTO;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {

        return bookRepository.findAll().stream()
                .map(BookMapper::toBookDTO)
                .toList();
    }

    public BookDTO getDtoById(Long id) {
        return bookRepository.findById(id).stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .map(BookMapper::toBookDTO)
                .orElse(null);
    }

    public BookDTO addBook(BookDTO book) {
        bookRepository.save(BookMapper.toBook(book));
        return book;
    }

    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return bookRepository.existsById(id);
    }

    public BookDTO updateBook(BookDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElse(null);
    }

    public BookPatchDTO patchBook(BookPatchDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (book.getTitle() != null) {
                        existingBook.setTitle(book.getTitle());
                    }
                    if (book.getAuthor() != null) {
                        existingBook.setAuthor(book.getAuthor());
                    }
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookPatchMapper.toBookPatchDTO(updatedBook);
                })
                .orElse(null);
    }
}
