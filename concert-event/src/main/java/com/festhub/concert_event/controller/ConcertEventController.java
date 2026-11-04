package com.festhub.concert_event.controller;

import com.festhub.concert_event.model.ConcertEvent;
import com.festhub.concert_event.service.ConcertEventService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class ConcertEventController {

    private final ConcertEventService service;

    public ConcertEventController(ConcertEventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConcertEvent> create(
            @Valid @RequestBody CreateConcertEventRequest request) {

        ConcertEvent event = service.create(
                request.id(),
                request.name(),
                request.description(),
                request.availableTickets(),
                request.ticketPriceCents());

        return ResponseEntity.status(201).body(event);
    }

    @GetMapping("/{id}")
    public ConcertEvent get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<ConcertEvent> getAll() {
        return service.getAll();
    }
}