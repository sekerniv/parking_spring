// FirestoreService.java
package com.example.service;

import com.example.model.ParkingLot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FirestoreService {

  public List<ParkingLot> fetchLots() throws Exception {
    Firestore db = FirestoreClient.getFirestore();
    var snapshot = db.collection("parkingLots").get().get();

    List<ParkingLot> lots = new ArrayList<>();
    for (DocumentSnapshot doc : snapshot.getDocuments()) {
      Map<String, Object> data = doc.getData();
      if (data == null) continue;

      ParkingLot lot = new ParkingLot();
      lot.setId(doc.getId());
      lot.setName(String.valueOf(data.getOrDefault("name", "Unnamed lot")));

      // ---- location: support {location:{lat,lng}} OR GeoPoint OR top-level lat/lng
      Double lat = null, lng = null;
      Object loc = data.get("location");
      if (loc instanceof Map<?,?> m) {
        lat = toDouble(m.get("lat"));
        lng = toDouble(m.get("lng"));
      } else if (loc instanceof GeoPoint gp) {
        lat = gp.getLatitude();
        lng = gp.getLongitude();
      }
      if (lat == null) lat = toDouble(data.get("lat"));
      if (lng == null) lng = toDouble(data.get("lng"));
      if (lat == null || lng == null) continue; // skip invalid docs
      lot.setLat(lat);
      lot.setLng(lng);

      // ---- hourly: prefer nested {hourly:{hourPrice}}, fallback to top-level hourPrice/pricePerHour
      ParkingLot.Hourly hourly = null;
      Object hourlyObj = data.get("hourly");
      if (hourlyObj instanceof Map<?,?> hm) {
        Double hp = toDouble(hm.get("hourPrice"));
        if (hp == null) hp = toDouble(hm.get("pricePerHour"));
        if (hp != null) {
          hourly = new ParkingLot.Hourly();
          hourly.setHourPrice(hp);
        }
      } else {
        Double hp = toDouble(data.get("hourPrice"));
        if (hp == null) hp = toDouble(data.get("pricePerHour"));
        if (hp != null) {
          hourly = new ParkingLot.Hourly();
          hourly.setHourPrice(hp);
        }
      }
      lot.setHourly(hourly);

      // ---- flatRate: {flatRate:{price, times:[{fromDay,fromHour,toDay,toHour}]}}
      Object flatObj = data.get("flatRate");
      if (flatObj instanceof Map<?,?> fm) {
        ParkingLot.FlatRate fr = new ParkingLot.FlatRate();
        fr.setPrice(toDouble(fm.get("price")));

        Object timesObj = fm.get("times");
        if (timesObj instanceof List<?> list) {
          List<ParkingLot.FlatWindow> windows = new ArrayList<>();
          for (Object o : list) {
            if (o instanceof Map<?,?> wm) {
              ParkingLot.FlatWindow w = new ParkingLot.FlatWindow();
              w.setFromDay(toInt(wm.get("fromDay")));
              w.setFromHour(toInt(wm.get("fromHour")));
              w.setToDay(toInt(wm.get("toDay")));
              w.setToHour(toInt(wm.get("toHour")));
              windows.add(w);
            }
          }
          fr.setTimes(windows);
        }

        // Only set if something meaningful exists
        if (fr.getPrice() != null || (fr.getTimes() != null && !fr.getTimes().isEmpty())) {
          lot.setFlatRate(fr);
        }
      }

      // ---- resident discount: prefer 'residentDiscount' (0..1).
      // Fallback: 'residentDiscountPercent' (0..1 or 0..100); normalize to 0..1.
      Double rd = toDouble(data.get("residentDiscount"));
      if (rd == null) {
        Double rdp = toDouble(data.get("residentDiscountPercent"));
        if (rdp != null) rd = (rdp > 1.0) ? (rdp / 100.0) : rdp;
      }
      if (rd != null) lot.setResidentDiscount(rd);

      lots.add(lot);
    }
    return lots;
  }

  private Double toDouble(Object o) {
    if (o == null) return null;
    if (o instanceof Number n) return n.doubleValue();
    try { return Double.parseDouble(o.toString()); } catch (Exception e) { return null; }
  }

  private Integer toInt(Object o) {
    if (o == null) return null;
    if (o instanceof Number n) return n.intValue();
    try { return Integer.parseInt(o.toString()); } catch (Exception e) { return null; }
  }
}
