package com.festhub.booking_category.controller;

import com.festhub.booking_category.model.BookingCategory;
import com.festhub.booking_category.service.BookingCategoryService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category")
public class BookingCategoryController {

    private final BookingCategoryService service;

    public BookingCategoryController(
            BookingCategoryService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingCategory> create(
            @Valid @RequestBody CreateBookingCategoryRequest request) {

        BookingCategory category = service.create(
                request.name(),
                request.description());

        return ResponseEntity
                .status(201)
                .body(category);
    }

    @GetMapping("/{id}")
    public BookingCategory get(
            @PathVariable UUID id) {

        return service.get(id);
    }

    @GetMapping
    public List<BookingCategory> getAll() {

        return service.getAll();
    }

    @PutMapping("/{id}")
    public BookingCategory update(
            @PathVariable UUID id,
            @RequestBody UpdateBookingCategoryRequest request) {

        return service.update(
                id,
                request.name(),
                request.description());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}