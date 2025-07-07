package com.example.library.model;

public record BookPatchDTO(String title, String author) {
    public static BookPatchDTO from(Book book) {
        return new BookPatchDTO(book.getTitle(), book.getAuthor());
    }
}
