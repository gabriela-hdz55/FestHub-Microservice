package com.festhub.concert_event.service;

import org.springframework.stereotype.Service;

import com.festhub.concert_event.exception.ConcertEventNotFoundException;
import com.festhub.concert_event.model.ConcertEvent;
import com.festhub.concert_event.repository.ConcertEventRepository;
import com.festhub.concert_event.clients.BookingCategoryClient;
import com.festhub.concert_event.clients.ArtistClient;

import java.util.List;
import java.util.UUID;
import java.util.Set;

@Service
public final class ConcertEventService {

    private static final int MAX_NAME_LENGTH = 2000;
    private static final int MAX_DESCRIPTION_LENGTH = 10000;
    private static final long MAX_SOLD_OUT_PRICE_CENTS = 10000L;

    private final ConcertEventRepository repository;
    private final BookingCategoryClient categoryClient;
    private final ArtistClient artistClient;

    public ConcertEventService(
            ConcertEventRepository repository,
            BookingCategoryClient categoryClient,
            ArtistClient artistClient) {

        this.repository = repository;
        this.categoryClient = categoryClient;
        this.artistClient = artistClient;
    }

    public ConcertEvent create(
            UUID id,
            String name,
            String description,
            int availableTickets,
            long ticketPriceCents,
            Set<UUID> artistIds,
            Set<UUID> categoryIds) {

        validate(name, description, availableTickets, ticketPriceCents);

        UUID eventId = id == null ? UUID.randomUUID() : id;

        if (repository.existsById(eventId)) {
            throw new IllegalArgumentException(
                    "Concert event already exists: " + eventId);
        }

        validateArtists(artistIds);
        validateCategories(categoryIds);

        ConcertEvent event = new ConcertEvent(
                eventId,
                name,
                description,
                availableTickets,
                ticketPriceCents);

        event.setArtistIds(artistIds);
        event.setCategoryIds(categoryIds);

        return repository.save(event);
    }

    public ConcertEvent get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ConcertEventNotFoundException(id));
    }

    public List<ConcertEvent> getAll() {
        return repository.findAll();
    }

    public static final long MAX_ARTIST_TICKET_PRICE_SUM_CENTS = 50_000_000L;

    private void validateCategories(Set<UUID> categoryIds) {
        for (UUID categoryId : categoryIds) {
            categoryClient.validateExists(categoryId);
        }
    }

    private void validateArtists(Set<UUID> artistIds) {
        for (UUID artistId : artistIds) {
            artistClient.validateExists(artistId);
        }
    }

    private static void validate(
            String name,
            String description,
            int availableTickets,
            long ticketPriceCents) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Concert event name cannot be null or blank");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Concert event name cannot exceed "
                            + MAX_NAME_LENGTH
                            + " characters");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Concert event description cannot be null or blank");
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Concert event description cannot exceed "
                            + MAX_DESCRIPTION_LENGTH
                            + " characters");
        }

        if (availableTickets < 0) {
            throw new IllegalArgumentException(
                    "Available tickets cannot be negative");
        }

        if (ticketPriceCents <= 0) {
            throw new IllegalArgumentException(
                    "Ticket price must be greater than zero");
        }

        if (availableTickets == 0 && ticketPriceCents > MAX_SOLD_OUT_PRICE_CENTS) {
            throw new IllegalArgumentException(
                    "Sold-out concert event ticket price cannot exceed "
                            + MAX_SOLD_OUT_PRICE_CENTS
                            + " cents");
        }
    }
}
