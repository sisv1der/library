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
        return bookRepository.findById(id)
                .map(BookMapper::toBookDTO)
                .orElse(null);
    }

    public BookDTO addBook(BookDTO book) {
        return BookMapper.toBookDTO(bookRepository.save(BookMapper.toBook(book)));
    }

    public boolean deleteBookById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public BookDTO updateBook(BookDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.title());
                    existingBook.setAuthor(book.author());
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElse(null);
    }

    public BookPatchDTO patchBook(BookPatchDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (book.title() != null) {
                        existingBook.setTitle(book.title());
                    }
                    if (book.author() != null) {
                        existingBook.setAuthor(book.author());
                    }
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookPatchMapper.toBookPatchDTO(updatedBook);
                })
                .orElse(null);
    }
}
