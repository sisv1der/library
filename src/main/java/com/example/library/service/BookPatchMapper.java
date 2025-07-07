package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookPatchDTO;

public class BookPatchMapper {
    public static BookPatchDTO toBookPatchDTO(Book book) {
        if (book == null) {
            return null;
        }
        return BookPatchDTO.from(book);
    }
}
