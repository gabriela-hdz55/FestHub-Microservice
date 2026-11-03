package com.festhub.booking_category.controller;

import jakarta.validation.constraints.NotNull;

public record CreateBookingCategoryRequest(
                @NotNull String name,

                @NotNull String description) {
}