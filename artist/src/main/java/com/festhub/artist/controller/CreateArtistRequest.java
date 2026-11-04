package com.festhub.artist.controller;

public class CreateArtistRequest {
    private String name;
    private String bookingContact;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBookingContact() { return bookingContact; }
    public void setBookingContact(String bookingContact) { this.bookingContact = bookingContact; }

}
