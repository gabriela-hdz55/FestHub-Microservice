package com.festhub.promo_media.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdatePromoMediaRequest(
        @NotNull UUID eventId,
        @NotBlank String imageUrl) {
}
