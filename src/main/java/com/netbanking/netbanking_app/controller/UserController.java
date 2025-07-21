package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.UserService;
import com.netbanking.netbanking_app.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    private Long getUserIdFromPrincipal(Principal principal) {
        String username = principal.getName(); // assumes principal returns username
        User user = userService.getUserByUsername(username);
        return user.getUserId();
    }

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.register(user);

            auditLogService.logAction(
                    "REGISTER",
                    createdUser.getUsername(),
                    createdUser.getUserId(),
                    createdUser.getUsername(),
                    "New user registered via API",
                    false,
                    null
            );

            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Registration failed: " + e.getMessage());
        }
    }

    // ✅ View profile details
    @GetMapping("/profile/details")
    public String viewOnlyProfile(Model model, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        auditLogService.logAction(
                "PROFILE_VIEW",
                principal.getName(),
                user.getUserId(),
                user.getUsername(),
                "Viewed profile details",
                false,
                null
        );

        return "customer/profile-details";
    }

    // ✅ Manual login for Postman or mobile apps
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            User authenticatedUser = userService.login(username, password);

            auditLogService.logAction(
                    "LOGIN_API",
                    authenticatedUser.getUsername(),
                    authenticatedUser.getUserId(),
                    authenticatedUser.getUsername(),
                    "User authenticated via POST /api/users/login",
                    false,
                    null
            );

            return ResponseEntity.ok(authenticatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("❌ Login failed: " + e.getMessage());
        }
    }
}
