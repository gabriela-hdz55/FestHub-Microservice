package com.festhub.promo_media.service;

import com.festhub.promo_media.exception.PromoMediaNotFoundException;
import com.festhub.promo_media.model.PromoMedia;
import com.festhub.promo_media.repository.PromoMediaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PromoMediaService {

    private final PromoMediaRepository repository;

    public PromoMediaService(PromoMediaRepository repository) {
        this.repository = repository;
    }

    public PromoMedia create(UUID eventId, String imageUrl) {
        validate(eventId, imageUrl);
        return repository.save(
                new PromoMedia(UUID.randomUUID(), eventId, imageUrl));
    }

    public PromoMedia get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PromoMediaNotFoundException(id));
    }

    public List<PromoMedia> getAll() {
        return repository.findAll();
    }

    public PromoMedia update(UUID id, UUID eventId, String imageUrl) {
        validate(eventId, imageUrl);

        PromoMedia promoMedia = get(id);
        promoMedia.setEventId(eventId);
        promoMedia.setImageUrl(imageUrl);
        return repository.save(promoMedia);
    }

    public void delete(UUID id) {
        PromoMedia promoMedia = get(id);
        repository.delete(promoMedia);
    }

    private void validate(UUID eventId, String imageUrl) {
        if (eventId == null) {
            throw new IllegalArgumentException("Promo media event ID is required");
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Promo media image URL is required");
        }
    }
}
