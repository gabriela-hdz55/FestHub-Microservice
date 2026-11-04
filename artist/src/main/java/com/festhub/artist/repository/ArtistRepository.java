package com.festhub.artist.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.festhub.artist.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {
}