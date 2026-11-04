package com.festhub.concert_event.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "concert_event")
public class ConcertEvent {

        @Id
        private UUID id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String description;

        @Column(name = "available_tickets", nullable = false)
        private int availableTickets;

        @Column(name = "ticket_price_cents", nullable = false)
        private long ticketPriceCents;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "event_artist", joinColumns = @JoinColumn(name = "event_id"))
        @Column(name = "artist_id", nullable = false)
        private Set<UUID> artistIds = new HashSet<>();

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "event_category", joinColumns = @JoinColumn(name = "event_id"))
        @Column(name = "category_id", nullable = false)
        private Set<UUID> categoryIds = new HashSet<>();

        protected ConcertEvent() {
        }

        public ConcertEvent(
                        UUID id,
                        String name,
                        String description,
                        int availableTickets,
                        long ticketPriceCents) {

                this.id = id;
                this.name = name;
                this.description = description;
                this.availableTickets = availableTickets;
                this.ticketPriceCents = ticketPriceCents;
        }

        public UUID getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        public int getAvailableTickets() {
                return availableTickets;
        }

        public long getTicketPriceCents() {
                return ticketPriceCents;
        }

        public Set<UUID> getArtistIds() {
                return artistIds;
        }

        public Set<UUID> getCategoryIds() {
                return categoryIds;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public void setAvailableTickets(int availableTickets) {
                this.availableTickets = availableTickets;
        }

        public void setTicketPriceCents(long ticketPriceCents) {
                this.ticketPriceCents = ticketPriceCents;
        }

        public void setArtistIds(Set<UUID> artistIds) {
                Set<UUID> copy = new HashSet<>(artistIds);
                this.artistIds.clear();
                this.artistIds.addAll(copy);
        }

        public void setCategoryIds(Set<UUID> categoryIds) {
                Set<UUID> copy = new HashSet<>(categoryIds);
                this.categoryIds.clear();
                this.categoryIds.addAll(copy);
        }
}