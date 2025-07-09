package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookPutDTO;

@Deprecated
public class BookPutMapper {
    public static BookPutDTO toBookPutDTO(Book book) {
        if (book == null) {
            return null;
        }
        return BookPutDTO.from(book);
    }
}
