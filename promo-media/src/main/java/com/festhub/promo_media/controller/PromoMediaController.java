package com.festhub.promo_media.controller;

import com.festhub.promo_media.model.PromoMedia;
import com.festhub.promo_media.service.PromoMediaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class PromoMediaController {

    private final PromoMediaService service;

    public PromoMediaController(PromoMediaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PromoMedia> create(
            @Valid @RequestBody CreatePromoMediaRequest request) {
        PromoMedia promoMedia = service.create(
                request.eventId(),
                request.imageUrl());
        return ResponseEntity.status(201).body(promoMedia);
    }

    @GetMapping("/{id}")
    public PromoMedia get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<PromoMedia> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public PromoMedia update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePromoMediaRequest request) {
        return service.update(id, request.eventId(), request.imageUrl());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
