package com.example.library.service;

import com.example.library.model.*;

public class BookMapper {
    public static Book toBook(BookInput bookInput) {
        return switch (bookInput) {
            case BookDTO(Long id, String title, String author)  -> new Book(id, title, author);
            case BookPutDTO(String title, String author)        -> new Book(title, author);
            case BookPatchDTO(String title, String author)      -> new Book(title, author);
        };
    }

    public static BookDTO toBookDTO(Book book) {
        return BookDTO.from(book);
    }

    public static BookPutDTO toBookPutDTO(Book book) {
        return BookPutDTO.from(book);
    }

    public static BookPatchDTO toBookPatchDTO(Book book) {
        return BookPatchDTO.from(book);
    }
}
