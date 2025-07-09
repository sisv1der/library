package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;
import com.example.library.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class BookServiceTest {
    private BookService bookService;
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        Book book2 = new Book();

        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");

        Mockito.when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookDTO> bookDTOs = bookService.getAllBooks();

        Assertions.assertNotNull(bookDTOs);
        Assertions.assertEquals(2, bookDTOs.size());

        BookDTO result1 = bookDTOs.get(0);
        BookDTO result2 = bookDTOs.get(1);

        Assertions.assertEquals("Book 1", result1.title());
        Assertions.assertEquals("Author 1", result1.author());

        Assertions.assertEquals("Book 2", result2.title());
        Assertions.assertEquals("Author 2", result2.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getAllBooks_ShouldReturnEmptyList() {
        Mockito.when(bookRepository.findAll()).thenReturn(List.of());

        List<BookDTO> result = bookService.getAllBooks();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(bookRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        BookDTO result = bookService.getBookById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Book 1", result.title());
        Assertions.assertEquals("Author 1", result.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getBookById_ShouldReturn404_WhenBookNotFound() {
        Mockito.when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.getBookById(99L));
    }
}
