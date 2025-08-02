package com.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacyController {
    @GetMapping("/privacy")
    public String main(Model model, HttpSession session) throws Exception {
        return "privacy";
    }
}
