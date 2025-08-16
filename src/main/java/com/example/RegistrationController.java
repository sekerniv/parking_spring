package com.example;

import com.example.service.FirebaseAuthConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final FirebaseAuthConfig firebaseAuthConfig;

    public RegistrationController(FirebaseAuthConfig firebaseAuthConfig) {
        this.firebaseAuthConfig = firebaseAuthConfig;
    }

    @GetMapping
    public String registrationForm(Model model) {
        model.addAttribute("activeTab", "register");
        model.addAttribute("firebaseConfig", firebaseAuthConfig.getFirebaseConfig());
        return "register";
    }
}
