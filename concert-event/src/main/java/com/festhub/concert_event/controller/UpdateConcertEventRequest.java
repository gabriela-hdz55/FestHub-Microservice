package com.festhub.concert_event.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record UpdateConcertEventRequest(
        @NotBlank @Size(max = 2000) String name,

        @NotBlank @Size(max = 10000) String description,

        @NotNull @PositiveOrZero Integer availableTickets,

        @NotNull @Positive Long ticketPriceCents,

        @NotNull Set<@NotNull UUID> artistIds,

        @NotNull Set<@NotNull UUID> categoryIds) {
}