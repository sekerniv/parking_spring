package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAuthConfig {

    @Value("${firebase.apiKey:}")
    private String apiKey;

    @Value("${firebase.authDomain:}")
    private String authDomain;

    @Value("${firebase.projectId:}")
    private String projectId;

    @Value("${firebase.storageBucket:}")
    private String storageBucket;

    @Value("${firebase.messagingSenderId:}")
    private String messagingSenderId;

    @Value("${firebase.appId:}")
    private String appId;

    public Map<String, String> getFirebaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiKey", apiKey);
        config.put("authDomain", authDomain);
        config.put("projectId", projectId);
        config.put("storageBucket", storageBucket);
        config.put("messagingSenderId", messagingSenderId);
        config.put("appId", appId);
        return config;
    }
}
