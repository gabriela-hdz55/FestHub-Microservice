package com.festhub.artist.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "artist")
public class Artist {

    @Id
    private UUID id;

    @GeneratedValue(strategy = GenerationType.UUID)

    @Column(nullable = false)
    private String name;

    @Column(name = "booking_contact", nullable = false)
    private String bookingContact;

    protected Artist() {
    }

    public Artist(UUID id, String name, String bookingContact) {
        this.id = id;
        this.name = name;
        this.bookingContact = bookingContact;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getBookingContact() {
        return this.bookingContact;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setBookingContact(String bookingContact) {
        this.bookingContact = bookingContact;
    }
}