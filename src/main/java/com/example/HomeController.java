// HomeController.java
package com.example;

import com.example.model.LotResult;
import com.example.model.ParkingLot;
import com.example.model.User;
import com.example.service.DistanceService;
import com.example.service.FirestoreService;
import com.example.service.PricingService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

  private final FirestoreService firestoreService;
  private final DistanceService distanceService;
  private final PricingService pricingService;

  public HomeController(FirestoreService firestoreService, DistanceService distanceService, 
                       PricingService pricingService) {
    this.firestoreService = firestoreService;
    this.distanceService = distanceService;
    this.pricingService = pricingService;
  }

  // Default origin for first load (Tel Aviv)
  private static final double DEFAULT_LAT = 32.0853;
  private static final double DEFAULT_LNG = 34.7818;

    @GetMapping("/")
    public String home(Model model, HttpSession session,
                      @RequestParam(value = "resident", required = false) String residentParam,
                      @RequestParam(value = "lat", required = false) Double latParam,
                      @RequestParam(value = "lng", required = false) Double lngParam) throws Exception {
      // Get user from session, or null if not logged in
      User user = (User) session.getAttribute("user");
    
          final boolean isResident;
      double originLat = DEFAULT_LAT;
      double originLng = DEFAULT_LNG;
      String homeLocationLabel = "Tel Aviv";
      
      if (user != null) {
        
        isResident = residentParam == null? user.isResident() : residentParam.equals("true");
        
        if (user.getHomeLat() != null && user.getHomeLng() != null) {
          originLat = user.getHomeLat();
          originLng = user.getHomeLng();
          homeLocationLabel = user.getHomeAddress() != null && !user.getHomeAddress().isEmpty() 
              ? user.getHomeAddress() : "Your Home";
        }
      } else {
        // Guest user - use request parameters or default settings
        isResident = "true".equals(residentParam);
        
        // Use provided coordinates or default to Tel Aviv
        if (latParam != null && lngParam != null) {
          originLat = latParam;
          originLng = lngParam;
          homeLocationLabel = "Current Location";
        }
      }
    
    var lots = firestoreService.fetchLots();
    var distanceMap = distanceService.drivingDistances(originLat, originLng, lots);
    Instant now = Instant.now();

    List<LotResult> results = new ArrayList<>();
    for (ParkingLot lot : lots) {
      var d = distanceMap.get(lot.getId());
      String distanceText = (d == null)
          ? "—"
          : formatDistanceText(d.meters(), d.seconds());

          var prices = PricingService.DURATIONS_HOURS.stream()
          .map(h -> {
            var exit = now.plusSeconds(h * 3600L);
            int amount = pricingService.calculate(lot, now, exit, isResident);
            return LotResult.PriceCell.builder()
                .hours(h)
                .amount(amount)
                .label(h + "h: ₪ " + amount)
                .build();
          })
          .toList();

      results.add(LotResult.builder()
          .id(lot.getId())
          .name(lot.getName())
          .address(lot.getAddress()) // Add address instead of lat/lng
          .lat(lot.getLat())
          .lng(lot.getLng())
          .distanceText(distanceText)
          .prices(prices)
          .residentDiscount(lot.getResidentDiscount())
          .build());
    }

    // Sort by driving distance if available, otherwise by name
    results.sort(Comparator.comparingLong(r -> {
      // crude parse: prefer meters if present in distanceText like "1.2 km" or "850 m"
      String t = r.getDistanceText();
      if (t.endsWith(" km")) {
        double km = Double.parseDouble(t.replace(" km", ""));
        return (long) (km * 1000);
      } else if (t.endsWith(" m")) {
        return Long.parseLong(t.replace(" m", ""));
      }
      return Long.MAX_VALUE;
    }));

    model.addAttribute("results", results);
    model.addAttribute("activeTab", "home");
    model.addAttribute("homeLocation", java.util.Map.of("label", homeLocationLabel));
    model.addAttribute("user", user); // This can be null for guest users
    model.addAttribute("isLoggedIn", user != null);
    return "home";
  }

  private String formatDistanceText(long meters, long seconds) {
    // Prefer distance; if you want duration, switch this up
    if (meters >= 1000) return String.format("%.1f km", meters / 1000.0);
    return meters + " m";
  }
}
