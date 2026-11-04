package com.festhub.promo_media.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "promo_media")
public class PromoMedia {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    protected PromoMedia() {
    }

    public PromoMedia(UUID id, UUID eventId, String imageUrl) {
        this.id = id;
        this.eventId = eventId;
        this.imageUrl = imageUrl;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
