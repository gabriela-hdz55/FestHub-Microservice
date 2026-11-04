package com.festhub.promo_media.repository;

import com.festhub.promo_media.model.PromoMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromoMediaRepository extends JpaRepository<PromoMedia, UUID> {
}
