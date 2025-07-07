package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookDTO;

public class BookMapper {
    public static BookDTO toBookDTO(Book book) {
        return BookDTO.from(book);
    }

    public static Book toBook(BookDTO bookDTO) {
        return new Book(bookDTO.title(),
                bookDTO.author());
    }
}
