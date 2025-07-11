package com.example.library.service;

import com.example.library.model.*;
import com.example.library.repository.BookRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
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
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
    }

    @Transactional
    public BookDTO addBook(BookPostDTO book) {
        if (bookRepository.findByAuthorAndTitle(book.author(), book.title()).isPresent()) {
            throw new EntityExistsException("This book is already exists in database");
        }
        return BookMapper.toBookDTO(bookRepository.save(BookMapper.toBook(book)));
    }

    @Transactional
    public void deleteBookById(Long id) {
        if (bookRepository.existsById(id)) bookRepository.deleteById(id);
        else throw new EntityNotFoundException("Book not found.");
    }

    @Transactional
    public BookDTO updateBook(BookPutDTO book, Long id) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.title());
                    existingBook.setAuthor(book.author());
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
    }

    @Transactional
    public BookDTO patchBook(BookPatchDTO book, Long id) {
        if (isBookPatchDTOEmpty(book)) {
            throw new IllegalArgumentException("Nothing to update");
        }
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (StringUtils.isNotBlank(book.title())) {
                        existingBook.setTitle(book.title());
                    }
                    if (StringUtils.isNotBlank(book.author())) {
                        existingBook.setAuthor(book.author());
                    }
                    Book updatedBook = bookRepository.save(existingBook);
                    return BookMapper.toBookDTO(updatedBook);
                })
                .orElseThrow(() -> new EntityNotFoundException("Book not found."));
    }

    private boolean isBookPatchDTOEmpty(BookPatchDTO bookPatchDTO) {
        if (bookPatchDTO == null) return true;
        boolean isTitleEmpty = StringUtils.isBlank(bookPatchDTO.title());
        boolean isAuthorEmpty = StringUtils.isBlank(bookPatchDTO.author());
        return isTitleEmpty && isAuthorEmpty;
    }
}
