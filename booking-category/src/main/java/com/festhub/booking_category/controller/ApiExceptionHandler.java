package com.festhub.booking_category.controller;

import com.festhub.booking_category.exception.BookingCategoryNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BookingCategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            BookingCategoryNotFoundException exception) {

        return ResponseEntity
                .status(404)
                .body(
                        Map.of(
                                "error",
                                exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(
            IllegalArgumentException exception) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "error",
                                exception.getMessage()));
    }
}