// ParkingLot.java (add these nested types/fields)
package com.example.model;

import java.util.List;

public class ParkingLot {
  private String id;
  private String name;
  private Double lat;
  private Double lng;

  // Pricing schema equivalent to your JS
  private Hourly hourly;          // { hourPrice }
  private FlatRate flatRate;      // { price, times: [ { fromDay, fromHour, toDay, toHour } ] }
  private Double residentDiscount; // e.g., 0.75 means 75% off

  // Default constructor
  public ParkingLot() {}

  // All-args constructor
  public ParkingLot(String id, String name, Double lat, Double lng, Hourly hourly, FlatRate flatRate, Double residentDiscount) {
    this.id = id;
    this.name = name;
    this.lat = lat;
    this.lng = lng;
    this.hourly = hourly;
    this.flatRate = flatRate;
    this.residentDiscount = residentDiscount;
  }

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }

  public Double getLng() { return lng; }
  public void setLng(Double lng) { this.lng = lng; }

  public Hourly getHourly() { return hourly; }
  public void setHourly(Hourly hourly) { this.hourly = hourly; }

  public FlatRate getFlatRate() { return flatRate; }
  public void setFlatRate(FlatRate flatRate) { this.flatRate = flatRate; }

  public Double getResidentDiscount() { return residentDiscount; }
  public void setResidentDiscount(Double residentDiscount) { this.residentDiscount = residentDiscount; }

  public static class Hourly {
    private Double hourPrice;

    public Hourly() {}

    public Hourly(Double hourPrice) {
      this.hourPrice = hourPrice;
    }

    public Double getHourPrice() { return hourPrice; }
    public void setHourPrice(Double hourPrice) { this.hourPrice = hourPrice; }
  }

  public static class FlatRate {
    private Double price;
    private List<FlatWindow> times;

    public FlatRate() {}

    public FlatRate(Double price, List<FlatWindow> times) {
      this.price = price;
      this.times = times;
    }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<FlatWindow> getTimes() { return times; }
    public void setTimes(List<FlatWindow> times) { this.times = times; }
  }

  public static class FlatWindow {
    // 0=Sun … 6=Sat (same as JS Date.getDay())
    private Integer fromDay;
    private Integer fromHour; // 0–23
    private Integer toDay;
    private Integer toHour;   // 0–23

    public FlatWindow() {}

    public FlatWindow(Integer fromDay, Integer fromHour, Integer toDay, Integer toHour) {
      this.fromDay = fromDay;
      this.fromHour = fromHour;
      this.toDay = toDay;
      this.toHour = toHour;
    }

    public Integer getFromDay() { return fromDay; }
    public void setFromDay(Integer fromDay) { this.fromDay = fromDay; }

    public Integer getFromHour() { return fromHour; }
    public void setFromHour(Integer fromHour) { this.fromHour = fromHour; }

    public Integer getToDay() { return toDay; }
    public void setToDay(Integer toDay) { this.toDay = toDay; }

    public Integer getToHour() { return toHour; }
    public void setToHour(Integer toHour) { this.toHour = toHour; }
  }
}
