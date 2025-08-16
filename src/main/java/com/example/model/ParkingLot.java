// ParkingLot.java (add these nested types/fields)
package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class ParkingLot {
  private String id;
  private String name;
  private Double lat;
  private Double lng;

  // Pricing schema equivalent to your JS
  private Hourly hourly;          // { hourPrice }
  private FlatRate flatRate;      // { price, times: [ { fromDay, fromHour, toDay, toHour } ] }
  private Double residentDiscount; // e.g., 0.75 means 75% off

  @Data public static class Hourly { private Double hourPrice; }

  @Data public static class FlatRate {
    private Double price;
    private List<FlatWindow> times;
  }

  @Data public static class FlatWindow {
    // 0=Sun … 6=Sat (same as JS Date.getDay())
    private Integer fromDay;
    private Integer fromHour; // 0–23
    private Integer toDay;
    private Integer toHour;   // 0–23
  }
}
