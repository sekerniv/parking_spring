// FirebaseConfig.java
package com.example.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

  @PostConstruct
  public void init() throws Exception {
    if (FirebaseApp.getApps().isEmpty()) {
      // Either use GOOGLE_APPLICATION_CREDENTIALS pointing to a JSON file,
      // or place /serviceAccountKey.json on classpath and load it.
      String credsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

      GoogleCredentials credentials;
      if (StringUtils.hasText(credsPath)) {
        try (InputStream is = new FileInputStream(credsPath)) {
          credentials = GoogleCredentials.fromStream(is);
        }
      } else {
        try (InputStream is = getClass().getResourceAsStream("/serviceAccountKey.json")) {
          if (is == null) throw new IllegalStateException("Missing serviceAccountKey.json");
          credentials = GoogleCredentials.fromStream(is);
        }
      }

      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(credentials)
          .build();
      FirebaseApp.initializeApp(options);
    }
  }

  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Bean
  public Firestore firestore() {
    return FirestoreClient.getFirestore();
  }
}
