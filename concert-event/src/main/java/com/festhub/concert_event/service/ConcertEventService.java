package com.festhub.concert_event.service;

import com.festhub.concert_event.clients.ArtistClient;
import com.festhub.concert_event.clients.BookingCategoryClient;
import com.festhub.concert_event.exception.ConcertEventNotFoundException;
import com.festhub.concert_event.model.ConcertEvent;
import com.festhub.concert_event.repository.ConcertEventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ConcertEventService {

    private static final int MAX_NAME_LENGTH = 2000;
    private static final int MAX_DESCRIPTION_LENGTH = 10000;
    private static final long MAX_SOLD_OUT_PRICE_CENTS = 10000L;

    public static final long MAX_ARTIST_TICKET_PRICE_SUM_CENTS = 50_000_000L;

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

    @Transactional
    public ConcertEvent create(
            UUID id,
            String name,
            String description,
            int availableTickets,
            long ticketPriceCents,
            Set<UUID> artistIds,
            Set<UUID> categoryIds) {

        validate(name, description, availableTickets, ticketPriceCents);
        validateAssociationIds(artistIds, categoryIds);

        UUID eventId = id == null ? UUID.randomUUID() : id;

        if (repository.existsById(eventId)) {
            throw new IllegalArgumentException(
                    "Concert event already exists: " + eventId);
        }

        validateArtists(artistIds);
        validateCategories(categoryIds);
        validateArtistEventRules(
                eventId, name, ticketPriceCents, artistIds);

        ConcertEvent event = new ConcertEvent(
                eventId,
                name,
                description,
                availableTickets,
                ticketPriceCents);

        event.setArtistIds(new HashSet<>(artistIds));
        event.setCategoryIds(new HashSet<>(categoryIds));

        return repository.save(event);
    }

    @Transactional(readOnly = true)
    public ConcertEvent get(UUID id) {
        ConcertEvent event = findEvent(id);
        initializeAssociations(event);
        return event;
    }

    @Transactional(readOnly = true)
    public List<ConcertEvent> getAll() {
        List<ConcertEvent> events = repository.findAll();
        events.forEach(this::initializeAssociations);
        return events;
    }

    @Transactional
    public ConcertEvent update(
            UUID id,
            String name,
            String description,
            int availableTickets,
            long ticketPriceCents,
            Set<UUID> artistIds,
            Set<UUID> categoryIds) {

        ConcertEvent event = findEvent(id);

        validate(name, description, availableTickets, ticketPriceCents);
        validateAssociationIds(artistIds, categoryIds);

        validateArtists(artistIds);
        validateCategories(categoryIds);
        validateArtistEventRules(
                id, name, ticketPriceCents, artistIds);

        event.setName(name);
        event.setDescription(description);
        event.setAvailableTickets(availableTickets);
        event.setTicketPriceCents(ticketPriceCents);
        event.setArtistIds(new HashSet<>(artistIds));
        event.setCategoryIds(new HashSet<>(categoryIds));

        return repository.save(event);
    }

    @Transactional
    public void delete(UUID id) {
        ConcertEvent event = findEvent(id);
        repository.delete(event);
    }

    private ConcertEvent findEvent(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Concert event ID is required");
        }

        return repository.findById(id)
                .orElseThrow(
                        () -> new ConcertEventNotFoundException(id));
    }

    private void initializeAssociations(ConcertEvent event) {
        event.getArtistIds().size();
        event.getCategoryIds().size();
    }

    private void validateArtists(Set<UUID> artistIds) {
        for (UUID artistId : artistIds) {
            artistClient.validateExists(artistId);
        }
    }

    private void validateCategories(Set<UUID> categoryIds) {
        for (UUID categoryId : categoryIds) {
            categoryClient.validateExists(categoryId);
        }
    }

    private void validateArtistEventRules(
            UUID eventId,
            String name,
            long ticketPriceCents,
            Set<UUID> artistIds) {

        for (UUID artistId : artistIds) {
            if (repository.hasConflictingEventName(
                    artistId, eventId, name)) {

                throw new IllegalArgumentException(
                        "Artist " + artistId
                                + " already has another event named "
                                + name);
            }

            long otherPrices = repository.sumOtherEventTicketPrices(
                    artistId, eventId);

            if (ticketPriceCents > MAX_ARTIST_TICKET_PRICE_SUM_CENTS
                    || otherPrices > MAX_ARTIST_TICKET_PRICE_SUM_CENTS
                            - ticketPriceCents) {

                throw new IllegalArgumentException(
                        "This event would exceed the "
                                + "$500,000 ticket-price cap for artist "
                                + artistId);
            }
        }
    }

    private static void validateAssociationIds(
            Set<UUID> artistIds,
            Set<UUID> categoryIds) {

        if (artistIds == null || categoryIds == null) {
            throw new IllegalArgumentException(
                    "Artist and category ID sets are required");
        }

        for (UUID artistId : artistIds) {
            if (artistId == null) {
                throw new IllegalArgumentException(
                        "Artist IDs cannot contain null");
            }
        }

        for (UUID categoryId : categoryIds) {
            if (categoryId == null) {
                throw new IllegalArgumentException(
                        "Category IDs cannot contain null");
            }
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