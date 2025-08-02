package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginForm() {
        return "login";
    }

    @PostMapping
    public String processLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) throws Exception {

        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            session.setAttribute("username", username);
            return "redirect:/";
        } else {
            model.addAttribute("message", "Wrong details");
            return "login";
        }

    }
}
