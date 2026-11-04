package com.festhub.artist.controller;

import java.util.UUID;

public class UpdateArtistRequest {
    private UUID id;
    private String name;
    private String bookingContact;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBookingContact() { return bookingContact; }
    public void setBookingContact(String bookingContact) { this.bookingContact = bookingContact; }

}
