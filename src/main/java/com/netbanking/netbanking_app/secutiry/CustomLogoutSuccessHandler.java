package com.netbanking.netbanking_app.secutiry;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuditLogService auditLogService;
    private final UserService userService;

    public CustomLogoutSuccessHandler(AuditLogService auditLogService, UserService userService) {
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        if (authentication != null && authentication.getName() != null) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user != null) {
                String ipAddress = request.getRemoteAddr();

                // 📝 Log user logout into audit_logs
                auditLogService.logAction(
                        "LOGOUT_SUCCESS",              // Action type
                        username,                      // Performed by
                        user.getUserId(),              // Target user ID
                        username,                      // Target username
                        "User logged out from IP: " + ipAddress, // Description
                        false,                         // No notification required
                        null                           // No notification message
                );
            }
        }

        // ✅ Redirect after logout
        response.sendRedirect("/login?logout=true");
    }
}
