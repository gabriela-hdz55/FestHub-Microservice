package com.festhub.artist.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.festhub.artist.model.Artist;
import com.festhub.artist.service.ArtistService;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService service;

    public ArtistController(ArtistService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Artist> createArtist(@RequestBody CreateArtistRequest request) {
        Artist newArtist = new Artist(null, request.getName(), request.getBookingContact());
        return ResponseEntity.ok(service.createArtist(newArtist));
    }

    @GetMapping("/list")
    public Iterable<Artist> listArtists() {
        return service.listArtists();
    }

    @GetMapping("/read")
    public ResponseEntity<Artist> getArtist(@RequestParam UUID id) {
        return ResponseEntity.ok(service.getArtist(id));
    }

    @PostMapping("/update")
    public ResponseEntity<Artist> updateArtist(@RequestBody UpdateArtistRequest request) {
        Artist updatedData = new Artist(request.getId(), request.getName(), request.getBookingContact());
        return ResponseEntity.ok(service.updateArtist(request.getId(), updatedData));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteArtist(@RequestParam UUID id) {
        service.deleteArtist(id);
        return ResponseEntity.ok("Artist deleted successfully.");
    }
}