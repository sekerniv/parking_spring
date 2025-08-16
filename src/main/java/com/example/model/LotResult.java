package com.example.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LotResult {
  private String id;
  private String name;
  private double lat;
  private double lng;
  private String distanceText; // "1.2 km" or "8 min"
  private List<PriceCell> prices; // 12h/24h/72h

  @Data
  @Builder
  public static class PriceCell {
    private int hours;      // 12/24/72
    private int amount;     // rounded ₪
    private String label;   // "12h: ₪ 28"
  }
}
