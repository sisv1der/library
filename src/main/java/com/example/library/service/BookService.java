package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;
import com.example.library.model.BookPatchDTO;
import com.example.library.model.BookPutDTO;
import com.example.library.repository.BookRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
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

    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toBookDTO)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public BookDTO addBook(BookPutDTO book) {
        if (bookRepository.findByAuthorAndTitle(book.author(), book.title()).isPresent()) {
            throw new EntityExistsException("This book is already exists in database");
        }
        return BookMapper.toBookDTO(bookRepository.save(BookMapper.toBook(book)));
    }

    @Transactional
    public void deleteBookById(Long id) {
        if (bookRepository.existsById(id)) bookRepository.deleteById(id);
        else throw new EntityNotFoundException();
    }

    @Transactional
    public BookDTO updateBook(BookDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.title());
                    existingBook.setAuthor(book.author());
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public BookDTO patchBook(BookPatchDTO book, Long id) {
        if (book.title() == null && book.author() == null) {
            throw new IllegalArgumentException("Nothing to update");
        }
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (book.title() != null) {
                        existingBook.setTitle(book.title());
                    }
                    if (book.author() != null) {
                        existingBook.setAuthor(book.author());
                    }
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElseThrow(EntityNotFoundException::new);
    }
}
