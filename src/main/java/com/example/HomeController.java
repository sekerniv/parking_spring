package com.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("activeTab", "home");
        Object visits = session.getAttribute("visits");
        int count = (visits instanceof Integer) ? (Integer) visits : 0;
        session.setAttribute("visits", count + 1);
        model.addAttribute("counter", count + 1);
        return "home";
    }
}
