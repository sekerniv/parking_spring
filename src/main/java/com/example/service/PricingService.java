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
   * Calculate fee:
   * 1) If entry is inside a flat window -> charge flat once and jump to window end.
   * 2) From there, charge hourly (ceil to full hours) until exit.
   * If no flat window is active at entry -> pure hourly.
   * Resident discount (0..1) is applied to the final total only when isResident = true.
   */
  public int calculate(ParkingLot lot, Instant entry, Instant exit, boolean isResident) {
    if (exit.isBefore(entry)) exit = entry;

    final double hourlyRate = Optional.ofNullable(lot.getHourly())
        .map(Hourly::getHourPrice)
        .orElse(0.0);

    final FlatRate flat = lot.getFlatRate();
    final double discountRate = isResident
        ? Optional.ofNullable(lot.getResidentDiscount()).orElse(0.0)
        : 0.0;
    final double discountMultiplier = 1.0 - discountRate;

    ZonedDateTime entryZ = entry.atZone(ZONE);
    ZonedDateTime exitZ  = exit.atZone(ZONE);

    double total = 0.0;

    // --- Step 1: if currently inside a flat window, charge flat and jump to its end ---
    ZonedDateTime cursor = entryZ; // where billing continues after any flat
    if (flat != null && flat.getPrice() != null && flat.getTimes() != null && !flat.getTimes().isEmpty()) {
      FlatWindow active = findActiveFlatWindowAt(entry, flat.getTimes());
      if (active != null) {
        ZonedDateTime flatStart = occurrenceOnOrBefore(active.getFromDay(), active.getFromHour(), entry);
        ZonedDateTime flatEnd   = nextOccurrence(active.getToDay(),   active.getToHour(),   flatStart.toInstant());

        // We are guaranteed entryZ in [flatStart, flatEnd)
        total += flat.getPrice();
        cursor = flatEnd;

        // If exit is before the end of flat, we are done
        if (!exitZ.isAfter(cursor)) {
          return roundShekels(total * (isResident ? discountMultiplier : 1.0));
        }
      }
    }

    // --- Step 2: hourly from cursor -> exit (ceil to whole hours) ---
    long ms = Duration.between(cursor, exitZ).toMillis();
    long hours = ceilHours(ms);
    total += hourlyRate * hours;

    return roundShekels(total * (isResident ? discountMultiplier : 1.0));
  }

  /** Finds a flat window such that entry ∈ [windowStart, windowEnd). Returns null if none. */
  private FlatWindow findActiveFlatWindowAt(Instant entry, List<FlatWindow> windows) {
    ZonedDateTime entryZ = entry.atZone(ZONE);
    for (FlatWindow seg : windows) {
      if (seg == null) continue;
      ZonedDateTime start = occurrenceOnOrBefore(seg.getFromDay(), seg.getFromHour(), entry);
      ZonedDateTime end   = nextOccurrence(seg.getToDay(), seg.getToHour(), start.toInstant());
      if (!entryZ.isBefore(start) && entryZ.isBefore(end)) {
        return seg;
      }
    }
    return null;
  }

  /**
   * Previous-or-same occurrence of (day,hour) relative to reference (local time).
   * day: Firestore format (1=Sun, 2=Mon, ..., 7=Sat)
   */
  private ZonedDateTime occurrenceOnOrBefore(Integer day, Integer hour, Instant reference) {
    ZonedDateTime ref = reference.atZone(ZONE);
    int targetDay  = (day == null) ? ref.getDayOfWeek().getValue() : convertFirestoreDayToJava(day);
    int targetHour = (hour == null) ? ref.getHour() : clampHour(hour);

    ZonedDateTime date = ref.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);
    int currentDay = date.getDayOfWeek().getValue();
    int dayBack = (currentDay - targetDay + 7) % 7;
    return date.minusDays(dayBack);
  }

  /**
   * Next occurrence of (day,hour) strictly at/after the given reference (built from that reference's week).
   * day: Firestore format (1=Sun, 2=Mon, ..., 7=Sat)
   */
  private ZonedDateTime nextOccurrence(Integer day, Integer hour, Instant reference) {
    ZonedDateTime ref = reference.atZone(ZONE);
    int targetDay  = (day == null) ? ref.getDayOfWeek().getValue() : convertFirestoreDayToJava(day);
    int targetHour = (hour == null) ? ref.getHour() : clampHour(hour);

    ZonedDateTime date = ref.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);
    int currentDay = date.getDayOfWeek().getValue();
    int dayFwd = (targetDay - currentDay + 7) % 7;
    return date.plusDays(dayFwd);
  }

  /**
   * Convert Firestore day format (1=Sun, 2=Mon, ..., 7=Sat) to Java DayOfWeek format (1=Mon, 2=Tue, ..., 7=Sun)
   */
  private int convertFirestoreDayToJava(Integer firestoreDay) {
    if (firestoreDay == null) return 1; // Default to Monday
    // Firestore: 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat
    // Java:      1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun
    switch (firestoreDay) {
      case 1: return 7; // Sun -> 7
      case 2: return 1; // Mon -> 1
      case 3: return 2; // Tue -> 2
      case 4: return 3; // Wed -> 3
      case 5: return 4; // Thu -> 4
      case 6: return 5; // Fri -> 5
      case 7: return 6; // Sat -> 6
      default: return 1; // Default to Monday
    }
  }

  private int clampHour(int h) {
    return Math.max(0, Math.min(23, h));
  }

  private long ceilHours(long millis) {
    if (millis <= 0) return 0;
    return (long) Math.ceil(millis / 3_600_000.0);
  }

  private int roundShekels(double v) {
    return (int) Math.round(v);
  }
}
