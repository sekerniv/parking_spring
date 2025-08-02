package com.example;

import com.example.models.Book;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @GetMapping("/{id}")
    public String purchaseForm(@PathVariable int id, Model model, HttpSession session) throws Exception {
        Book book = null;

        String sql = "SELECT * FROM books WHERE id = ?";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            book = new Book();
            book.setId(rs.getInt("id"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setImage(rs.getString("image"));
            book.setPrice(rs.getDouble("price"));
        }
        model.addAttribute("book", book);
        model.addAttribute("username", session.getAttribute("username")); // <-- Add username for layout
        return "purchase";
    }

    @PostMapping("/{id}")
    public String processPurchase(@PathVariable int id,
            @RequestParam String CreditCard,
            @RequestParam String CVV,
            @RequestParam String CardDate,
            HttpSession session,
            Model model) {

        // Add username for layout on the thanks page too
        model.addAttribute("username", session.getAttribute("username"));

        /**
         * There are a few ways of doing it:
         * 1. Sending a redirect with "return 'redirect:/thanks' but that will require
         * also having a ThanksController
         * 2. returning the template with return 'thanks'
         * 3. making the thanks page static (removing any templating code) and move it
         * to /resources/static/
         **/
        return "thanks";
    }
}
