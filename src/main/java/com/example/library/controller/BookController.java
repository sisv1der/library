package com.example.library.controller;

import com.example.library.model.BookDTO;
import com.example.library.service.BookService;
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
        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        BookDTO book = bookService.getDtoById(id);
        if (book == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO book) {
        BookDTO newBook = bookService.addBook(book);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBookById(id);
        if (deleted) {
            return new ResponseEntity<>("Book deleted.", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO bookDto, @PathVariable Long id) {
        BookDTO oldBook = bookService.updateBook(bookDto, id);
        if (oldBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(oldBook, HttpStatus.OK);
    }

    /*@PatchMapping("/{id}")
    public ResponseEntity<Book> patchBook(@RequestBody Book book, @PathVariable Long id) {

    }*/
}
