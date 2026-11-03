package com.festhub.booking_category.exception;

import java.util.UUID;

public class BookingCategoryNotFoundException
        extends RuntimeException {

    public BookingCategoryNotFoundException(UUID id) {

        super(
                "Booking category not found: " + id);
    }
}