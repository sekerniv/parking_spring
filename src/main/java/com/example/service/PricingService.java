// PricingService.java
package com.example.service;

import com.example.model.ParkingLot;
import com.example.model.ParkingLot.FlatRate;
import com.example.model.ParkingLot.FlatWindow;
import com.example.model.ParkingLot.Hourly;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class PricingService {

  /** Preset durations to show in the UI (hours). */
  public static final List<Integer> DURATIONS_HOURS = List.of(12, 24, 72);

  /** Use local timezone like JS Date(). Change if you want a fixed zone. */
  private static final ZoneId ZONE = ZoneId.systemDefault();

  /**
   * Mirrors the JS calculateParkingFee(parkingLot, entryTime, exitTime, isResident).
   * - Uses hourly.hourPrice (₪/hour)
   * - Optional flatRate { price, times: [{ fromDay, fromHour, toDay, toHour }]}
   * - residentDiscount (0..1) applied only when isResident=true
   */
  public int calculate(ParkingLot lot, Instant entry, Instant exit, boolean isResident) {
    if (exit.isBefore(entry)) {
      // Guard: zero or minimal charge if exit < entry (shouldn’t happen, but keeps math sane)
      exit = entry;
    }

    double hourlyRate = Optional.ofNullable(lot.getHourly())
        .map(Hourly::getHourPrice)
        .orElse(0.0);

    FlatRate flat = lot.getFlatRate();
    double discountRate = isResident
        ? Optional.ofNullable(lot.getResidentDiscount()).orElse(0.0)
        : 0.0;
    double discountMultiplier = 1.0 - discountRate; // e.g. 0.25 when 75% off

    // Find a flat window active at entry
    FlatWindow activeFlat = null;
    if (flat != null && flat.getTimes() != null && !flat.getTimes().isEmpty()) {
      ZonedDateTime entryZ = entry.atZone(ZONE);
      for (FlatWindow seg : flat.getTimes()) {
        if (seg == null) continue;
        ZonedDateTime from = nextOccurrence(seg.getFromDay(), seg.getFromHour(), entry);
        ZonedDateTime to   = nextOccurrence(seg.getToDay(),   seg.getToHour(),   from.toInstant());
        if (!entryZ.isBefore(from) && entryZ.isBefore(to)) {
          activeFlat = seg;
          break;
        }
      }
    }

    double total;

    if (activeFlat != null && flat != null && flat.getPrice() != null) {
      // In a flat window at entry
      ZonedDateTime from = nextOccurrence(activeFlat.getFromDay(), activeFlat.getFromHour(), entry);
      ZonedDateTime to   = nextOccurrence(activeFlat.getToDay(),   activeFlat.getToHour(),   from.toInstant());

      if (!exit.atZone(ZONE).isAfter(to)) {
        // Entire stay within the flat window
        total = flat.getPrice();
      } else {
        // Flat until 'to', then hourly afterwards (ceil to full hours)
        long msAfterFlat = Duration.between(to, exit.atZone(ZONE)).toMillis();
        long hoursAfter  = ceilHours(msAfterFlat);
        total = flat.getPrice() + hourlyRate * hoursAfter;
      }
    } else {
      // Pure hourly (ceil to full hours)
      long ms = Duration.between(entry, exit).toMillis();
      long hours = ceilHours(ms);
      total = hourlyRate * hours;
    }

    return (int) Math.round(total * (isResident ? discountMultiplier : 1.0));
  }

  /**
   * JS nextOccurrence(day, hour, reference)
   * day: 0=Sun … 6=Sat (matches JS Date.getDay()).
   * Sets time to given hour:00 local, then advances to the next target weekday from the reference.
   */
  private ZonedDateTime nextOccurrence(Integer day, Integer hour, Instant reference) {
    ZonedDateTime ref = reference.atZone(ZONE);

    if (day == null || hour == null) {
      // Fallback: clamp hour and zero minutes/seconds today
      int h = hour == null ? ref.getHour() : clampHour(hour);
      return ref.withHour(h).withMinute(0).withSecond(0).withNano(0);
    }

    int targetDay = Math.floorMod(day, 7);      // 0..6
    int targetHour = clampHour(hour);           // 0..23

    ZonedDateTime date = ref.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);

    // Java: MONDAY=1..SUNDAY=7 → convert to 0..6 with Sunday=0 to match JS
    int currentDay = date.getDayOfWeek().getValue() % 7;
    int dayDiff = (targetDay - currentDay + 7) % 7;

    return date.plusDays(dayDiff);
  }

  private int clampHour(int h) {
    return Math.max(0, Math.min(23, h));
  }

  private long ceilHours(long millis) {
    if (millis <= 0) return 0;
    return (long) Math.ceil(millis / 3_600_000.0);
  }
}
