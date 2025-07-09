package com.example.library.service;

import com.example.library.model.*;

public class BookMapper {
    public static Book toBook(BookInput bookInput) {
        return switch (bookInput) {
            case BookDTO(Long id, String title, String author)  -> new Book(id, title, author);
            case BookPostDTO(String title, String author)        -> new Book(title, author);
            case BookPatchDTO(String title, String author)      -> new Book(title, author);
        };
    }

    public static BookDTO toBookDTO(Book book) {
        return BookDTO.from(book);
    }
}
