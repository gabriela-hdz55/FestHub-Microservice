package com.festhub.artist.service;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.festhub.artist.exception.ArtistNotFoundException;
import com.festhub.artist.model.Artist;
import com.festhub.artist.repository.ArtistRepository;

@Service
public class ArtistService {

    private final ArtistRepository repository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public ArtistService(ArtistRepository repository) {
        this.repository = repository;
    }

    public Artist createArtist(Artist artist) {
        validateEmail(artist.getBookingContact());
        return repository.save(artist);
    }

    public Artist getArtist(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }

    public Artist updateArtist(UUID id, Artist updatedData) {
        Artist existing = getArtist(id);
        
        if (updatedData.getName() != null && !updatedData.getName().isBlank()) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getBookingContact() != null && !updatedData.getBookingContact().isBlank()) {
            validateEmail(updatedData.getBookingContact());
            existing.setBookingContact(updatedData.getBookingContact());
        }
        
        return repository.save(existing);
    }

    public void deleteArtist(UUID id) {
        Artist existing = getArtist(id);
        repository.delete(existing);
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Booking contact must be a valid email format.");
        }
    }
}