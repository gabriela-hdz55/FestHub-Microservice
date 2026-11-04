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
                request.ticketPriceCents(),
                request.artistIds(),
                request.categoryIds());

        return ResponseEntity.status(201).body(event);
    }

    @PutMapping("/{id}")
    public ConcertEvent update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConcertEventRequest request) {

        return service.update(
                id,
                request.name(),
                request.description(),
                request.availableTickets(),
                request.ticketPriceCents(),
                request.artistIds(),
                request.categoryIds());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ConcertEvent get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<ConcertEvent> getAll() {
        return service.getAll();
    }

    @GetMapping("/by-artist/{artistId}/ids")
    public List<UUID> getEventIdsForArtist(
            @PathVariable UUID artistId) {

        return service.getEventIdsForArtist(artistId);
    }

    @GetMapping("/by-category/{categoryId}/ids")
    public List<UUID> getEventIdsForCategory(
            @PathVariable UUID categoryId) {

        return service.getEventIdsForCategory(categoryId);
    }
}