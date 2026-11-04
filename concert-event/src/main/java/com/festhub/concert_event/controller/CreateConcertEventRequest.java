package com.festhub.concert_event.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.util.Set;

public record CreateConcertEventRequest(
        UUID id,

        @NotBlank @Size(max = 2000) String name,

        @NotBlank @Size(max = 10000) String description,

        @NotNull @PositiveOrZero Integer availableTickets,

        @NotNull @Positive Long ticketPriceCents,

        Set<@NotNull UUID> artistIds,

        Set<@NotNull UUID> categoryIds) {

    public CreateConcertEventRequest {
        artistIds = artistIds == null ? Set.of() : artistIds;
        categoryIds = categoryIds == null ? Set.of() : categoryIds;
    }
}