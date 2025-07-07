package com.example.library.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookDTO(@NotNull(message = "Id must not be empty!")Long id,
                      @NotBlank(message = "Title must not be empty!") String title,
                      @NotBlank(message = "Author must not be empty!") String author) {
    public static BookDTO from(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor());
    }
}
