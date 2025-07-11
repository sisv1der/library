package com.example.library.integration.controller;

import com.example.library.model.*;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    void cleanDB() {
        bookRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BookRepository bookRepository;

    @Test
    void getAllBooks_ShouldReturnOk() throws Exception {
        bookRepository.save(new Book("title1", "author1"));
        bookRepository.save(new Book("title2", "author2"));

        mockMvc.perform(get("/books/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].author").value(containsInAnyOrder("author1", "author2")))
                .andExpect(jsonPath("$[*].title").value(containsInAnyOrder("title1", "title2")));
    }

    @Test
    void getBookById_ShouldReturnOk_WhenBookExists() throws Exception {
        Book savedBook = bookRepository.save(new Book("title1", "author1"));

        mockMvc.perform(get("/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedBook.getTitle()))
                .andExpect(jsonPath("$.author").value(savedBook.getAuthor()))
                .andExpect(jsonPath("$.id").value(savedBook.getId()));

        Assertions.assertThat(bookRepository.findById(savedBook.getId())).get().isEqualTo(savedBook);
    }

    @Test
    void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/books/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
        Assertions.assertThat(bookRepository.findById(999L)).isNotPresent();
    }

    @Test
    void getBookById_ShouldReturnBadRequest_WhenParameterIsInvalid() throws Exception {
        mockMvc.perform(get("/books/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        String.format("Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                        "abc",
                        "id",
                        "Long"
                        )))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void addBook_ShouldReturnCreated_WhenValid() throws Exception {
        BookPostDTO postDTO = new BookPostDTO("Title", "Author");
        BookDTO responseDTO = new BookDTO(1L, "Title", "Author");
        String json = objectMapper.writeValueAsString(postDTO);

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(responseDTO.title()))
                .andExpect(jsonPath("$.author").value(responseDTO.author()));

        Assertions.assertThat(bookRepository.count()).isEqualTo(1);
        Book savedBook = bookRepository.findAll().getFirst();
        Assertions.assertThat(savedBook).isNotNull();
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(responseDTO.title());
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(responseDTO.author());
    }

    @Test
    void addBook_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        String json = """
                {
                }
                """;

        mockMvc.perform(post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void addBook_ShouldReturnBadRequest_WhenAnyFieldIsEmpty() throws Exception {
        BookPostDTO postDTO = new BookPostDTO("", null);
        String json = objectMapper.writeValueAsString(postDTO);

        mockMvc.perform(post("/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[*].field").value(containsInAnyOrder(
                        "title",
                        "author")))
                .andExpect(jsonPath("$.fieldErrors[*].message").value(containsInAnyOrder(
                        "Author must not be empty!",
                        "Title must not be empty!"
                )));

        Assertions.assertThat(bookRepository.count()).isEqualTo(0);
    }

    @Test
    void addBook_ShouldReturnConflict_WhenBookIsAlreadyExists() throws Exception {
        bookRepository.save(new Book("Title", "Author"));

        Assertions.assertThat(bookRepository.count()).isEqualTo(1);

        BookPostDTO postDTO = new BookPostDTO("Title", "Author");
        String json = objectMapper.writeValueAsString(postDTO);

        mockMvc.perform(post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This book is already exists in database"))
                .andExpect(jsonPath("$.errorCode").value("ENTITY_EXISTS"));

        Assertions.assertThat(bookRepository.count()).isEqualTo(1);
    }

    @Test
    void deleteBookById_ShouldReturnOk_WhenBookIsDeleted() throws Exception {
        Book savedBook = bookRepository.save(new Book("Title", "Author"));

        Assertions.assertThat(bookRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertThat(bookRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteBookById_ShouldReturnNotFound_WhenBookIsNotDeleted() throws Exception {
        Assertions.assertThat(bookRepository.findById(999L)).isNotPresent();

        mockMvc.perform(delete("/books/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void deleteBookById_ShouldReturnBadRequest_WhenParameterIsInvalid() throws Exception {
        mockMvc.perform(delete("/books/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        String.format("Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                                "abc",
                                "id",
                                "Long"
                        )))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void updateBookById_ShouldReturnOk_WhenBookIsUpdated() throws Exception {
        Book savedBook = bookRepository.save(new Book("Title", "Author"));

        Assertions.assertThat(bookRepository.count()).isEqualTo(1);
        BookPutDTO putDTO = new BookPutDTO("TitleUpdated", "AuthorUpdated");

        String json = objectMapper.writeValueAsString(putDTO);

        mockMvc.perform(put("/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(putDTO.title()))
                .andExpect(jsonPath("$.author").value(putDTO.author()));

        Assertions.assertThat(bookRepository.findById(savedBook.getId()).get().getTitle()).isEqualTo(putDTO.title());
    }

    @Test
    void updateBookById_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        Book savedBook = bookRepository.save(new Book("Title", "Author"));
        
        String json = """
                {
                }
                """;

        mockMvc.perform(put("/books/{id}", savedBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void updateBookById_ShouldReturnNotFound_WhenBookNotFound() throws Exception {
        Assertions.assertThat(bookRepository.findById(999L)).isNotPresent();

        BookPutDTO putDTO = new BookPutDTO("TitleUpdated", "AuthorUpdated");

        String json = objectMapper.writeValueAsString(putDTO);

        mockMvc.perform(put("/books/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void updateBookById_ShouldReturnBadRequest_WhenParameterIsInvalid() throws Exception {
        BookPutDTO putDTO = new BookPutDTO("TitleUpdated", "AuthorUpdated");

        String json = objectMapper.writeValueAsString(putDTO);

        mockMvc.perform(put("/books/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        String.format("Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                                "abc",
                                "id",
                                "Long"
                        )))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    void patchBookById_ShouldReturnOk_WhenBookIsUpdated() throws Exception {
        Book savedBook = bookRepository.save(new Book("Title", "Author"));
        BookPatchDTO patchDTO = new BookPatchDTO("TitleUpdated", "");

        String json = objectMapper.writeValueAsString(patchDTO);

        Assertions.assertThat(bookRepository.findById(savedBook.getId())).isPresent();

        mockMvc.perform(patch("/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(patchDTO.title()))
                .andExpect(jsonPath("$.author").value(savedBook.getAuthor()));

        Assertions.assertThat(bookRepository.findById(savedBook.getId()).get().getTitle()).isEqualTo(patchDTO.title());
    }

    @Test
    void patchBookById_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        Book savedBook = bookRepository.save(new Book("Title", "Author"));

        String json = "";

        mockMvc.perform(patch("/books/{id}", savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Required request body is missing")))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));

        Assertions.assertThat(bookRepository.findById(savedBook.getId()).get().getTitle()).isEqualTo(savedBook.getTitle());
    }

    @Test
    void patchBookById_ShouldReturnNotFound_WhenBookNotFound() throws Exception {
        Assertions.assertThat(bookRepository.findById(999L)).isNotPresent();

        BookPatchDTO patchDTO = new BookPatchDTO("TitleUpdated", "");

        String json = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/books/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found."))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void patchBookById_ShouldReturnBadRequest_WhenParameterIsInvalid() throws Exception {
        BookPatchDTO patchDTO = new BookPatchDTO("TitleUpdated", "");
        String json = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/books/{id}", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        String.format("Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                                "abc",
                                "id",
                                "Long"
                        )))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }
}
