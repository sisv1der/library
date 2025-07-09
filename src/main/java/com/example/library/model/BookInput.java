package com.example.library.model;

public sealed interface BookInput permits BookDTO, BookPostDTO, BookPatchDTO, BookPutDTO {}