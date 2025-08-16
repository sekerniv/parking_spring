package com.example;

import com.example.model.User;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settings(Model model, HttpSession session) {
        // Get user from session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Redirect to login if not authenticated
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("activeTab", "settings");
        return "settings";
    }

    @PostMapping("/settings")
    public String updateSettings(
            @RequestParam("homeAddress") String homeAddress,
            @RequestParam(value = "isResident", required = false) String isResident,
            Model model,
            HttpSession session) {

        // Get user from session
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        String userId = currentUser.getId();
        String userEmail = currentUser.getEmail();

        // Parse resident status
        boolean residentStatus = "on".equals(isResident);

        // For now, we'll use default coordinates. In a real app, you'd geocode the
        // address
        Double homeLat = 32.0853; // Default Tel Aviv coordinates
        Double homeLng = 34.7818;

        User updatedUser = userService.createOrUpdateUser(
                userId,
                userEmail,
                homeAddress,
                homeLat,
                homeLng,
                residentStatus);

        if (updatedUser != null) {
            model.addAttribute("successMessage", "Settings updated successfully!");
            // Update the user in session
            session.setAttribute("user", updatedUser);
        } else {
            model.addAttribute("errorMessage", "Failed to update settings. Please try again.");
        }

        model.addAttribute("user", updatedUser != null ? updatedUser : userService.getUserById(userId));
        model.addAttribute("activeTab", "settings");
        return "settings";
    }
}
