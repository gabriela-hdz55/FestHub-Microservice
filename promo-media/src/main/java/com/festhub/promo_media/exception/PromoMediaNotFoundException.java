package com.festhub.promo_media.exception;

import java.util.UUID;

public class PromoMediaNotFoundException extends RuntimeException {

    public PromoMediaNotFoundException(UUID id) {
        super("Promo media not found: " + id);
    }
}
