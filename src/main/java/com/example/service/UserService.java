package com.example.service;

import com.example.model.User;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    private final Firestore firestore;
    private static final String USERS_COLLECTION = "users";

    public User getUserById(String userId) {
        try {
            var doc = firestore.collection(USERS_COLLECTION).document(userId).get().get();
            if (doc.exists()) {
                return doc.toObject(User.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            // Log error and return null
        }
        return null;
    }

    public User saveUser(User user) {
        try {
            firestore.collection(USERS_COLLECTION).document(user.getId()).set(user).get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            // Log error and return null
            return null;
        }
    }

    public User createOrUpdateUser(String userId, String email, String homeAddress, Double homeLat, Double homeLng, boolean isResident) {
        User user = User.builder()
                .id(userId)
                .email(email)
                .homeAddress(homeAddress)
                .homeLat(homeLat)
                .homeLng(homeLng)
                .isResident(isResident)
                .build();
        
        return saveUser(user);
    }
}
