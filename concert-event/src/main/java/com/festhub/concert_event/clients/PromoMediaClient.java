package com.festhub.concert_event.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class PromoMediaClient {

    private final RestClient client;

    public PromoMediaClient(
            @Value("${services.media.url}") String baseUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // ============================================================
    // MEDIA RESPONSE
    // ============================================================

    public record MediaRecord(
            UUID id,
            UUID eventId,
            String imageUrl) {
    }

    // ============================================================
    // FIND MEDIA FOR AN EVENT
    // ============================================================

    public List<MediaRecord> getForEvent(UUID eventId) {

        try {
            MediaRecord[] records = client.get()
                    .uri("/api/media")
                    .retrieve()
                    .body(MediaRecord[].class);

            if (records == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Media service returned no response body");
            }

            return Arrays.stream(records)
                    .filter(media -> eventId.equals(media.eventId()))
                    .toList();

        } catch (RestClientException exception) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not retrieve event media",
                    exception);
        }
    }

    // ============================================================
    // DELETE MEDIA
    // ============================================================

    public void deleteIfPresent(UUID mediaId) {

        try {
            client.delete()
                    .uri("/api/media/{id}", mediaId)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException exception) {

            if (exception.getStatusCode().value() == 404) {
                return;
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Event deleted, but media cleanup failed: "
                            + mediaId,
                    exception);

        } catch (RestClientException exception) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Event deleted, but Media service is unavailable",
                    exception);
        }
    }
}