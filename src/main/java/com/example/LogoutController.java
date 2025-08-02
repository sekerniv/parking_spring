package com.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        System.out.println("[Logout] User '" + (username != null ? username : "unknown") + "' logged out");
        session.invalidate();
        return "redirect:/";
    }
}
