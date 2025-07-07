package com.example.library.controller;

import com.example.library.model.BookDTO;
import com.example.library.model.BookPatchDTO;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books/")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BookDTO>> getBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        BookDTO book = bookService.getDtoById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO book) {
        BookDTO newBook = bookService.addBook(book);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@Valid @RequestBody BookDTO bookDto, @PathVariable Long id) {
        BookDTO oldBook = bookService.updateBook(bookDto, id);
        return new ResponseEntity<>(oldBook, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookPatchDTO> patchBook(@RequestBody BookPatchDTO bookDto, @PathVariable Long id) {
        BookPatchDTO oldBook = bookService.patchBook(bookDto, id);
        return new ResponseEntity<>(oldBook, HttpStatus.OK);
    }
}
