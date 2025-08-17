package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;

import java.util.Optional;

@Service
public class GeocodingService {

    private final WebClient webClient;
    private final String googleApiKey;

    public GeocodingService(@Value("${GOOGLE_API_KEY:}") String googleApiKey) {
        this.googleApiKey = googleApiKey;
        this.webClient = WebClient.builder().build();
    }

    /**
     * Geocode an address to get latitude and longitude coordinates
     * @param address The address to geocode
     * @return Optional containing lat/lng coordinates, or empty if geocoding failed
     */
    public Optional<Coordinates> geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return Optional.empty();
        }

        if (googleApiKey.trim().isEmpty()) {
            throw new IllegalStateException("GOOGLE_API_KEY is not set");
        }

        try {
            String uri = "https://maps.googleapis.com/maps/api/geocode/json" +
                    "?address=" + java.net.URLEncoder.encode(address.trim(), "UTF-8") +
                    "&key=" + googleApiKey;

            JsonNode response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, r -> r.createException())
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && "OK".equals(response.path("status").asText())) {
                JsonNode results = response.path("results");
                if (results.isArray() && results.size() > 0) {
                    JsonNode location = results.get(0).path("geometry").path("location");
                    double lat = location.path("lat").asDouble();
                    double lng = location.path("lng").asDouble();
                    return Optional.of(new Coordinates(lat, lng));
                }
            }
        } catch (Exception e) {
            // Log the error but don't throw - return empty instead
            System.err.println("Geocoding failed for address '" + address + "': " + e.getMessage());
        }

        return Optional.empty();
    }

    public static class Coordinates {
        private final double lat;
        private final double lng;

        public Coordinates(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() { return lat; }
        public double getLng() { return lng; }
    }
}
