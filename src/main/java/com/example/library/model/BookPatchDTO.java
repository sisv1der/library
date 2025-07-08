package com.example.library.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookPatchDTO(
        @Schema(description = "The title of the book", example = "Sherlock Holmes")
        String title,
        @Schema(description = "The author of the book", example = "Arthur Conan Doyle")
        String author) {
    public static BookPatchDTO from(Book book) {
        return new BookPatchDTO(book.getTitle(), book.getAuthor());
    }
}
