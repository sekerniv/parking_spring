package com.example;

import com.example.models.Book;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// line 12
@Controller
public class HomeController {

// 31sdf
// I am here :)
    @GetMapping("/")
    public String books(Model model, HttpSession session) throws Exception {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        Connection conn = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Book b = new Book();
            b.setId(rs.getInt("id"));
            b.setTitle(rs.getString("title"));
            b.setAuthor(rs.getString("author"));
            b.setImage(rs.getString("image"));
            b.setPrice(rs.getDouble("price"));
            books.add(b);
        }

        model.addAttribute("books", books);
        model.addAttribute("username", session.getAttribute("username"));

        return "home";
    }
}
