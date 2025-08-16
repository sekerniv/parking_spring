package com.example;

import com.example.model.User;
import com.example.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> request, HttpSession session) {
        try {
            String idToken = request.get("idToken");
            if (idToken == null) {
                return ResponseEntity.badRequest().body("No token provided");
            }

            // Verify the Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            // Get or create user in our database
            User user = userService.getUserById(uid);
            if (user == null) {
                // Create new user
                user = User.builder()
                        .id(uid)
                        .email(email)
                        .homeAddress("")
                        .homeLat(null)
                        .homeLng(null)
                        .isResident(false)
                        .build();
                userService.saveUser(user);
            }

            // Store user in session
            session.setAttribute("user", user);
            session.setAttribute("username", email);

            return ResponseEntity.ok().body("Authentication successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
        }
    }
}
