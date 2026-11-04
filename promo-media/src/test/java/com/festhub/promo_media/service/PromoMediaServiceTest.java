package com.festhub.promo_media.service;

import com.festhub.promo_media.exception.PromoMediaNotFoundException;
import com.festhub.promo_media.model.PromoMedia;
import com.festhub.promo_media.repository.PromoMediaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromoMediaServiceTest {

    private final PromoMediaRepository repository =
            mock(PromoMediaRepository.class);
    private final PromoMediaService service =
            new PromoMediaService(repository);

    @Test
    void createSavesMediaWithGeneratedIdAndGivenDetails() {
        UUID eventId = UUID.randomUUID();
        String imageUrl = "https://example.test/festival.jpg";
        when(repository.save(any(PromoMedia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PromoMedia created = service.create(eventId, imageUrl);

        assertNotNull(created.getId());
        assertEquals(eventId, created.getEventId());
        assertEquals(imageUrl, created.getImageUrl());
        verify(repository).save(created);
    }

    @Test
    void getThrowsWhenMediaDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PromoMediaNotFoundException.class, () -> service.get(id));
    }

    @Test
    void createRejectsBlankImageUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(UUID.randomUUID(), " "));
        verifyNoInteractions(repository);
    }

    @Test
    void updateChangesEventAndImageUrl() {
        UUID id = UUID.randomUUID();
        PromoMedia existing = new PromoMedia(
                id,
                UUID.randomUUID(),
                "https://example.test/old.jpg");
        UUID updatedEventId = UUID.randomUUID();
        String updatedImageUrl = "https://example.test/new.jpg";
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        PromoMedia updated = service.update(id, updatedEventId, updatedImageUrl);

        assertEquals(id, updated.getId());
        assertEquals(updatedEventId, updated.getEventId());
        assertEquals(updatedImageUrl, updated.getImageUrl());
        verify(repository).save(existing);
    }

    @Test
    void deleteRemovesExistingMedia() {
        UUID id = UUID.randomUUID();
        PromoMedia existing = new PromoMedia(
                id,
                UUID.randomUUID(),
                "https://example.test/festival.jpg");
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id);

        verify(repository).delete(existing);
    }
}
