package com.example.library.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record BookPostDTO(@Schema(description = "The title of the book", example = "Sherlock Holmes")
                         @NotBlank(message = "Title must not be empty!") String title,
                          @Schema(description = "The author of the book", example = "Arthur Conan Doyle")
                         @NotBlank(message = "Author must not be empty!") String author) implements BookInput {
    public static BookPostDTO from(Book book) {
        return new BookPostDTO(book.getTitle(), book.getAuthor());
    }
}
