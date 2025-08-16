package com.example.model;

import java.util.List;

public class LotResult {
  private String id;
  private String name;
  private double lat;
  private double lng;
  private String distanceText; // "1.2 km" or "8 min"
  private List<PriceCell> prices; // 12h/24h/72h

  // Default constructor
  public LotResult() {}

  // All-args constructor
  public LotResult(String id, String name, double lat, double lng, String distanceText, List<PriceCell> prices) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lng = lng;
    this.distanceText = distanceText;
    this.prices = prices;
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public double getLat() { return lat; }
  public void setLat(double lat) { this.lat = lat; }

  public double getLng() { return lng; }
  public void setLng(double lng) { this.lng = lng; }

  public String getDistanceText() { return distanceText; }
  public void setDistanceText(String distanceText) { this.distanceText = distanceText; }

  public List<PriceCell> getPrices() { return prices; }
  public void setPrices(List<PriceCell> prices) { this.prices = prices; }

  // Builder pattern
  public static LotResultBuilder builder() {
    return new LotResultBuilder();
  }

  public static class LotResultBuilder {
    private String id;
    private String name;
    private double lat;
    private double lng;
    private String distanceText;
    private List<PriceCell> prices;

    public LotResultBuilder id(String id) { this.id = id; return this; }
    public LotResultBuilder name(String name) { this.name = name; return this; }
    public LotResultBuilder lat(double lat) { this.lat = lat; return this; }
    public LotResultBuilder lng(double lng) { this.lng = lng; return this; }
    public LotResultBuilder distanceText(String distanceText) { this.distanceText = distanceText; return this; }
    public LotResultBuilder prices(List<PriceCell> prices) { this.prices = prices; return this; }

    public LotResult build() {
      return new LotResult(id, name, lat, lng, distanceText, prices);
    }
  }

  public static class PriceCell {
    private int hours;      // 12/24/72
    private int amount;     // rounded ₪
    private String label;   // "12h: ₪ 28"

    // Default constructor
    public PriceCell() {}

    // All-args constructor
    public PriceCell(int hours, int amount, String label) {
      this.hours = hours;
      this.amount = amount;
      this.label = label;
    }

    // Getters and Setters
    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    // Builder pattern
    public static PriceCellBuilder builder() {
      return new PriceCellBuilder();
    }

    public static class PriceCellBuilder {
      private int hours;
      private int amount;
      private String label;

      public PriceCellBuilder hours(int hours) { this.hours = hours; return this; }
      public PriceCellBuilder amount(int amount) { this.amount = amount; return this; }
      public PriceCellBuilder label(String label) { this.label = label; return this; }

      public PriceCell build() {
        return new PriceCell(hours, amount, label);
      }
    }
  }
}
