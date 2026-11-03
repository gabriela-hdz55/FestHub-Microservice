package com.festhub.booking_category.controller;

public record UpdateBookingCategoryRequest(
        String name,
        String description) {
}