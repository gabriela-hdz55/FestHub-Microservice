package com.festhub.concert_event.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class BookingCategoryClient {

    private final RestClient client;

    public BookingCategoryClient(
            @Value("${services.category.url}") String baseUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public void validateExists(UUID categoryId) {
        try {
            client.get()
                    .uri("/api/category/{id}", categoryId)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new IllegalArgumentException(
                        "Booking category does not exist: " + categoryId);
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Category service could not validate the category",
                    exception);

        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Category service is unavailable",
                    exception);
        }
    }
}