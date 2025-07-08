package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;
import com.example.library.model.BookPatchDTO;
import com.example.library.repository.BookRepository;
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

    public BookDTO getDtoById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toBookDTO)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public BookDTO addBook(BookDTO book) {
        if (book == null) throw new IllegalArgumentException("BookDTO is null");
        log.info()
        return BookMapper.toBookDTO(bookRepository.save(BookMapper.toBook(book)));
    }

    @Transactional
    public void deleteBookById(Long id) {
        if (bookRepository.existsById(id)) bookRepository.deleteById(id);
        else throw new EntityNotFoundException();
    }

    @Transactional
    public BookDTO updateBook(BookDTO book, Long id) {
        if (book == null) throw new IllegalArgumentException("BookDTO is null");

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
    public BookPatchDTO patchBook(BookPatchDTO book, Long id) {
        if (book == null || book.title() == null && book.author() == null) {
            throw new IllegalArgumentException("BookPatchDTO or BookPatchDTO's fields is null");
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
                    return BookPatchMapper.toBookPatchDTO(updatedBook);
                })
                .orElseThrow(EntityNotFoundException::new);
    }
}
