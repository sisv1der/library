package com.example.library.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Represents a book in the system.")
public record BookDTO(@NotNull(message = "Id must not be empty!")
                      @Schema(description = "The unique identifier of the book", example = "1")
                      Long id,
                      @Schema(description = "The title of the book", example = "Sherlock Holmes")
                      @NotBlank(message = "Title must not be empty!") String title,
                      @Schema(description = "The author of the book", example = "Arthur Conan Doyle")
                      @NotBlank(message = "Author must not be empty!") String author) {
    public static BookDTO from(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }
}
