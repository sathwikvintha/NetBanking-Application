package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.Card;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.CardService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    // 🔹 View cards (Thymeleaf page)
    @GetMapping("/cards")
    public String viewCards(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("Authenticated user not found: " + username);
        }

        List<Card> cards = cardService.getCardsByUserId(user.getUserId());

        model.addAttribute("cards", cards);
        model.addAttribute("card", new Card()); // pre-fill form
        return "customer/card-form";
    }

    // 🔹 View cards as JSON (for API clients)
    @GetMapping("/cards/json")
    @ResponseBody
    public ResponseEntity<List<Card>> getCardsJson(Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Card> cards = cardService.getCardsByUserId(user.getUserId());
        return ResponseEntity.ok(cards);
    }

    // 🔹 Add card via form submission
    @PostMapping("/cards/add")
    public String addCard(@ModelAttribute("card") Card card, Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("Authenticated user not found: " + username);
        }

        // ✅ Use the relationship mapping, not userId
        card.setUser(user);

        // Optional debug log
        System.out.println("🟢 Creating card for user: " + user.getUsername() + " [ID: " + user.getUserId() + "]");

        cardService.addCard(card);
        return "redirect:/customer/cards";
    }
}
