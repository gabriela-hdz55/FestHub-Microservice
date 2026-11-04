package com.festhub.concert_event.exception;

import java.util.UUID;

public class ConcertEventNotFoundException extends RuntimeException {

    public ConcertEventNotFoundException(UUID id) {
        super("Concert event not found: " + id);
    }
}