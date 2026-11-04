package com.festhub.concert_event.repository;

import com.festhub.concert_event.model.ConcertEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConcertEventRepository
        extends JpaRepository<ConcertEvent, UUID> {
}