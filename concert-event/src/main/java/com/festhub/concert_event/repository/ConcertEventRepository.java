package com.festhub.concert_event.repository;

import com.festhub.concert_event.model.ConcertEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ConcertEventRepository
        extends JpaRepository<ConcertEvent, UUID> {
    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM concert_event ce
                JOIN event_artist ea ON ea.event_id = ce.id
                WHERE ea.artist_id = :artistId
                  AND ce.id <> :eventId
                  AND ce.name = :name
            )
            """, nativeQuery = true)
    boolean hasConflictingEventName(
            @Param("artistId") UUID artistId,
            @Param("eventId") UUID eventId,
            @Param("name") String name);

    @Query(value = """
            SELECT COALESCE(SUM(ce.ticket_price_cents), 0)
            FROM concert_event ce
            JOIN event_artist ea ON ea.event_id = ce.id
            WHERE ea.artist_id = :artistId
              AND ce.id <> :eventId
            """, nativeQuery = true)
    long sumOtherEventTicketPrices(
            @Param("artistId") UUID artistId,
            @Param("eventId") UUID eventId);
}