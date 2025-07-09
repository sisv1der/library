package com.example.library.model;

public sealed interface BookInput permits BookDTO, BookPutDTO, BookPatchDTO {}