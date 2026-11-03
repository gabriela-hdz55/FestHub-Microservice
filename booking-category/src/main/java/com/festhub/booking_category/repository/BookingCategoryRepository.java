package com.festhub.booking_category.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.festhub.booking_category.model.BookingCategory;

public interface BookingCategoryRepository extends JpaRepository<BookingCategory, UUID> {

}
