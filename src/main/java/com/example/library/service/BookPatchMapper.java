package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.BookPatchDTO;

public class BookPatchMapper {
    public static Book toBook(BookPatchDTO bookPatchDTO) {
        if (bookPatchDTO == null) {
            return null;
        }
        Book book = new Book();
        book.setTitle(bookPatchDTO.getTitle());
        book.setAuthor(bookPatchDTO.getAuthor());
        return book;
    }

    public static BookPatchDTO toBookPatchDTO(Book book) {
        if (book == null) {
            return null;
        }
        BookPatchDTO bookPatchDTO = new BookPatchDTO();
        bookPatchDTO.setTitle(book.getTitle());
        bookPatchDTO.setAuthor(book.getAuthor());
        return bookPatchDTO;
    }
}
