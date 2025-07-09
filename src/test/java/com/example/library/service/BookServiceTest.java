package com.example.library.service;

import com.example.library.model.*;
import com.example.library.repository.BookRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

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

        Mockito.when(bookRepository.findAll())
                .thenReturn(List.of(book1, book2));

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
        Mockito.when(bookRepository.findAll())
                .thenReturn(List.of());

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

        Mockito.when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book1));

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

        Mockito.verify(bookRepository, Mockito.times(1)).findById(99L);
    }

    @Test
    void addBook_ShouldAddBook_WhenBookNotFound() {
        BookPostDTO book1 = new BookPostDTO("Book 1", "Author 1");

        Mockito.when(bookRepository.findByAuthorAndTitle("Author 1", "Book 1"))
                .thenReturn(Optional.empty());
        Mockito.when(bookRepository.save(any()))
                .thenReturn(new Book(1L, "Book 1", "Author 1"));

        BookDTO result = bookService.addBook(book1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.id());
        Assertions.assertEquals("Book 1", result.title());
        Assertions.assertEquals("Author 1", result.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findByAuthorAndTitle("Author 1", "Book 1");
        Mockito.verify(bookRepository, Mockito.times(1)).save(any());
    }

    @Test
    void addBook_ShouldReturn409_WhenBookAlreadyExists() {
        BookPostDTO book1 = new BookPostDTO("Book 1", "Author 1");

        Mockito.when(bookRepository.findByAuthorAndTitle("Author 1", "Book 1"))
                .thenReturn(Optional.of(new Book(1L, "Book 1", "Author 1")));

        Assertions.assertThrows(EntityExistsException.class, () -> bookService.addBook(book1));

        Mockito.verify(bookRepository, Mockito.times(1)).findByAuthorAndTitle("Author 1", "Book 1");
        Mockito.verify(bookRepository, Mockito.never()).save(any());
    }

    @Test
    void deleteBookById_ShouldDeleteBook_WhenBookExists() {
        Book book1 = new Book(1L, "Book 1", "Author 1");

        Mockito.when(bookRepository.existsById(book1.getId()))
                .thenReturn(true);

        bookService.deleteBookById(book1.getId());

        Mockito.verify(bookRepository, Mockito.times(1)).existsById(book1.getId());
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(book1.getId());
    }

    @Test
    void deleteBookById_ShouldReturn404_WhenBookNotFound() {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.deleteBookById(1L));

        Mockito.verify(bookRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(bookRepository, Mockito.never()).deleteById(1L);
    }

    @Test
    void updateBookById_ShouldUpdateBook_WhenBookExists() {
        Book oldBook = new Book(1L, "Book 1", "Author 1");
        BookPutDTO newBook = new BookPutDTO("Book 2", "Author 2");

        Mockito.when(bookRepository.findById(1L))
                .thenReturn(Optional.of(oldBook));
        Mockito.when(bookRepository.save(oldBook))
                .thenReturn(oldBook);

        BookDTO result = bookService.updateBook(newBook, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.id());
        Assertions.assertEquals("Book 2", result.title());
        Assertions.assertEquals("Author 2", result.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookRepository, Mockito.times(1)).save(oldBook);
    }

    @Test
    void updateBookById_ShouldReturn404_WhenBookNotFound() {
        Long ID = 1L;
        BookPutDTO newBook = new BookPutDTO("Book 1", "Author 1");

        Mockito.when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(newBook, ID));

        Mockito.verify(bookRepository, Mockito.times(1)).findById(ID);
        Mockito.verify(bookRepository, Mockito.never()).save(any());
    }

    @Test
    void patchBookById_ShouldPatchBook_WhenOneFieldIsBlank() {
        BookPatchDTO bookPatchDTO = new BookPatchDTO("Book 2", "");
        Long oldBookId = 1L;
        Book oldBook =  new Book(oldBookId, "Book 1", "Author 1");

        Mockito.when(bookRepository.findById(oldBookId))
                .thenReturn(Optional.of(oldBook));
        Mockito.when(bookRepository.save(oldBook))
                .thenReturn(new Book(oldBookId, bookPatchDTO.title(), oldBook.getAuthor()));

        BookDTO result = bookService.patchBook(bookPatchDTO, oldBookId);
        Assertions.assertNotNull(result);

        Assertions.assertEquals(oldBookId, result.id());
        Assertions.assertEquals(bookPatchDTO.title(), result.title());
        Assertions.assertEquals("Author 1", result.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findById(oldBookId);
        Mockito.verify(bookRepository, Mockito.times(1)).save(oldBook);
    }

    @Test
    void patchBookById_ShouldPatchBook_WhenOneFieldIsNull() {
        BookPatchDTO bookPatchDTO = new BookPatchDTO(null, "Title 2");
        Long oldBookId = 1L;
        Book oldBook =  new Book(oldBookId, "Book 1", "Author 1");

        Mockito.when(bookRepository.findById(oldBookId))
                .thenReturn(Optional.of(oldBook));
        Mockito.when(bookRepository.save(oldBook))
                .thenReturn(oldBook);

        BookDTO result = bookService.patchBook(bookPatchDTO, oldBookId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(oldBookId, result.id());
        Assertions.assertNotEquals(bookPatchDTO.title(), result.title());
        Assertions.assertEquals(bookPatchDTO.author(), result.author());

        Mockito.verify(bookRepository, Mockito.times(1)).findById(oldBookId);
        Mockito.verify(bookRepository, Mockito.times(1)).save(oldBook);
    }

    @Test
    void patchBookById_ShouldReturn400_WhenBothFieldsAreNull() {
        BookPatchDTO bookPatchDTO = new BookPatchDTO(null, "");
        Long oldBookId = 1L;
        Book oldBook =  new Book(oldBookId, "Book 1", "Author 1");

        Mockito.when(bookRepository.findById(oldBookId))
                .thenReturn(Optional.of(oldBook));

        Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.patchBook(bookPatchDTO, oldBookId));

        Mockito.verify(bookRepository, Mockito.never()).findById(oldBookId);
        Mockito.verify(bookRepository, Mockito.never()).save(oldBook);
    }

    @Test
    void patchBookById_ShouldReturn404_WhenBookNotFound() {
        BookPatchDTO bookPatchDTO = new BookPatchDTO("Book 2", null);
        Long oldBookId = 1L;

        Mockito.when(bookRepository.findById(oldBookId))
                .thenThrow(new EntityNotFoundException());

        Assertions.assertThrows(EntityNotFoundException.class, () -> bookService.patchBook(bookPatchDTO, oldBookId));

        Mockito.verify(bookRepository, Mockito.times(1)).findById(oldBookId);
        Mockito.verify(bookRepository, Mockito.never()).save(any());
    }
}
