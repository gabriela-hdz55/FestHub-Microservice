package com.festhub.booking_category.service;

import com.festhub.booking_category.exception.BookingCategoryNotFoundException;
import com.festhub.booking_category.model.BookingCategory;
import com.festhub.booking_category.repository.BookingCategoryRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BookingCategoryService {

    private final BookingCategoryRepository repository;

    public BookingCategoryService(
            BookingCategoryRepository repository) {

        this.repository = repository;
    }

    public BookingCategory create(
            String name,
            String description) {

        validate(name, description);

        UUID id = UUID.randomUUID();

        BookingCategory category = new BookingCategory(
                id,
                name,
                description);

        return repository.save(category);
    }

    public BookingCategory get(UUID id) {

        return repository
                .findById(id)
                .orElseThrow(
                        () -> new BookingCategoryNotFoundException(id));
    }

    public List<BookingCategory> getAll() {

        return repository.findAll();
    }

    public BookingCategory update(
            UUID id,
            String name,
            String description) {

        validate(name, description);

        BookingCategory category = get(id);

        category.setName(name);
        category.setDescription(description);

        return repository.save(category);
    }

    public void delete(UUID id) {

        BookingCategory category = get(id);

        repository.delete(category);
    }

    private void validate(
            String name,
            String description) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Booking category name is required");
        }

        if (description == null ||
                description.isBlank()) {

            throw new IllegalArgumentException(
                    "Booking category description is required");
        }
    }
}