package com.example.library.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;


@Schema(description = "Standard error response for API requests.")
public record ErrorResponse(

        @Schema(description = "Timestamp of the error", example = "2023-07-08T14:30:00Z")
        String timestamp,
        @Schema(description = "Detailed message describing the error", example = "Invalid input data")
        String message,
        @Schema(description = "Error code representing the type of error", example = "400")
        String errorCode,
        @Schema(description = "List of field-specific error details, if applicable")
        List<FieldErrorResponse> fieldErrors) {
    public ErrorResponse(String message, String errorCode, List<FieldErrorResponse> fieldErrors) {
        this(LocalDateTime.now().toString(), message, errorCode, fieldErrors);
    }

    public static ErrorResponse of(String message, String errorCode, List<FieldErrorResponse> fieldErrors) {
        return new ErrorResponse(message, errorCode, fieldErrors);
    }
}