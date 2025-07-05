package com.example.library.model;

import jakarta.validation.constraints.NotBlank;

public class BookDTO {
    @NotBlank(message = "Id must not be empty!")
    private final Long id;
    @NotBlank(message = "Title must not be empty!")
    private String title;
    @NotBlank(message = "Author must not be empty!")
    private String author;

    public BookDTO(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
