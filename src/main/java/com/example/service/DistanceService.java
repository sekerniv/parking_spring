// DistanceService.java
package com.example.service;

import com.example.model.ParkingLot;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatusCode;

import java.util.*;

@Service
public class DistanceService {

  private static final int MAX_DEST_PER_REQUEST = 25; // Google Distance Matrix per-request dest limit
  private final WebClient webClient;

  public DistanceService(WebClient webClient) {
    this.webClient = webClient;
  }

  @Value("${GOOGLE_API_KEY:}")
  private String googleApiKey;

  /**
   * Returns map of lotId -> (meters, seconds) for driving distance from
   * (originLat, originLng).
   * Lots without a valid result are omitted from the map.
   */
  public Map<String, DistanceEntry> drivingDistances(
      double originLat, double originLng, List<ParkingLot> lots) {

    if (lots == null || lots.isEmpty())
      return Collections.emptyMap();
    if (!StringUtils.hasText(googleApiKey)) {
      throw new IllegalStateException("GOOGLE_API_KEY is not set");
    }

    Map<String, DistanceEntry> out = new HashMap<>(lots.size());

    // Batch by 25 destinations to respect API limits
    for (int start = 0; start < lots.size(); start += MAX_DEST_PER_REQUEST) {
      int end = Math.min(start + MAX_DEST_PER_REQUEST, lots.size());
      List<ParkingLot> batch = lots.subList(start, end);

      // Build destinations param: "lat,lng|lat,lng|..."
      StringBuilder destinations = new StringBuilder();
      for (int i = 0; i < batch.size(); i++) {
        if (i > 0)
          destinations.append('|');
        destinations.append(batch.get(i).getLat())
            .append(',')
            .append(batch.get(i).getLng());
      }

      String uri = UriComponentsBuilder
          .fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
          .queryParam("origins", originLat + "," + originLng)
          .queryParam("destinations", destinations.toString()) // raw, includes '|'
          .queryParam("mode", "driving")
          .queryParam("key", googleApiKey)
          .build(false) // <-- let Spring encode reserved chars like '|'
          .toUriString();

      JsonNode resp = webClient.get()
          .uri(uri)
          .retrieve()
          .onStatus(HttpStatusCode::isError, r -> r.createException())
          .bodyToMono(JsonNode.class)
          .onErrorResume(ex -> {
            // log and skip this batch
            System.err.println("Distance Matrix call failed: " + ex.getMessage());
            return reactor.core.publisher.Mono.empty();
          })
          .blockOptional()
          .orElse(null);

      if (resp == null)
        continue;
      if (!"OK".equals(resp.path("status").asText("OK"))) {
        // Top-level status may be something like "OVER_QUERY_LIMIT"; skip this batch
        // gracefully
        continue;
      }

      JsonNode rows = resp.path("rows");
      if (!rows.isArray() || rows.size() == 0)
        continue;

      JsonNode elements = rows.get(0).path("elements");
      for (int i = 0; i < elements.size() && i < batch.size(); i++) {
        JsonNode el = elements.get(i);
        if (!"OK".equals(el.path("status").asText()))
          continue;

        long meters = el.path("distance").path("value").asLong(0);
        long seconds = el.path("duration").path("value").asLong(0);
        if (meters <= 0 || seconds <= 0)
          continue;

        ParkingLot lot = batch.get(i);
        out.put(lot.getId(), new DistanceEntry(meters, seconds));
      }
    }

    return out;
  }

  public record DistanceEntry(long meters, long seconds) {
  }
}
