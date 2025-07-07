package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookPatchDTO;

public class BookPatchMapper {
    public static Book toBook(BookPatchDTO bookPatchDTO) {
        if (bookPatchDTO == null) {
            return null;
        }
        return new Book(bookPatchDTO.title(), bookPatchDTO.author());
    }

    public static BookPatchDTO toBookPatchDTO(Book book) {
        if (book == null) {
            return null;
        }
        return BookPatchDTO.from(book);
    }
}
