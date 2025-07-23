package com.netbanking.netbanking_app.secutiry;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import com.netbanking.netbanking_app.util.TokenGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditLogService auditLogService;
    private final UserService userService;

    public CustomAuthenticationSuccessHandler(AuditLogService auditLogService, UserService userService) {
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user != null) {
            String ipAddress = request.getRemoteAddr();

            // ✅ Log login event
            auditLogService.logAction(
                    "LOGIN_SUCCESS",
                    username,
                    user.getUserId(),
                    username,
                    "User logged in from IP: " + ipAddress,
                    false,
                    null
            );

            // ✅ Store userId in session
            request.getSession().setAttribute("userId", user.getUserId());

            // 🛡️ Generate session token and expiry
            String token = TokenGenerator.generateToken();
            request.getSession().setAttribute("loginToken", token);
            request.getSession().setAttribute("tokenExpiry", System.currentTimeMillis() + (20 * 60 * 1000)); // 20 minutes

            // 📋 Log token-based login
            auditLogService.logAction("USER_LOGGED_IN_WITH_TOKEN");
        } else {
            System.out.println("❌ Login success handler could not resolve user: " + username);
        }

        // 🎯 Role-based redirection
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    response.sendRedirect("/admin/verify-otp");
                    return;
                case "ROLE_CUSTOMER":
                    response.sendRedirect("/customer/verify-otp");
                    return;
            }
        }

        // 🧭 Fallback redirect
        response.sendRedirect("/");
    }
}
