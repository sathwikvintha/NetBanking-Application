package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.UserService;
import com.netbanking.netbanking_app.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Random;

@Controller
public class AdminOtpController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/admin/verify-otp")
    public String verifyAdminOtpPage(Principal principal, HttpServletRequest request, Model model) {
        String username = principal.getName();
        User admin = userService.findByUsername(username);

        if (admin == null) {
            model.addAttribute("error", "Admin user not found.");
            return "error";
        }

        int otp = new Random().nextInt(900_000) + 100_000;
        HttpSession session = request.getSession();
        session.setAttribute("adminOtp", otp);

        long expiryMillis = System.currentTimeMillis() + (5 * 60 * 1000);
        session.setAttribute("adminOtpExpiry", expiryMillis);

        notificationService.sendEmail(admin.getEmail(), "Admin Login OTP", "Your OTP is: " + otp + "\nValid for 5 minutes.");

        auditLogService.logAction("ADMIN_OTP_SENT");

        model.addAttribute("maskedEmail", maskEmail(admin.getEmail()));
        model.addAttribute("maskedPhone", maskPhone(admin.getPhone()));
        return "admin/verify-otp";
    }

    @PostMapping("/admin/validate-otp")
    public String validateAdminOtp(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Object expectedOtpObj = session.getAttribute("adminOtp");
        Object expiryObj = session.getAttribute("adminOtpExpiry");

        if (expectedOtpObj == null || expiryObj == null) {
            model.addAttribute("error", "OTP expired. Please request a new one.");
            return "admin/verify-otp";
        }

        int expectedOtp = (int) expectedOtpObj;
        long expiryTime = (long) expiryObj;
        long currentTime = System.currentTimeMillis();

        if (currentTime > expiryTime) {
            session.removeAttribute("adminOtp");
            session.removeAttribute("adminOtpExpiry");
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            return "admin/verify-otp";
        }

        String enteredStr = request.getParameter("enteredOtp");
        if (enteredStr == null || !enteredStr.matches("\\d{6}")) {
            model.addAttribute("error", "Enter a valid 6-digit OTP.");
            return "admin/verify-otp";
        }

        int enteredOtp = Integer.parseInt(enteredStr);

        if (enteredOtp == expectedOtp) {
            session.removeAttribute("adminOtp");
            session.removeAttribute("adminOtpExpiry");
            session.setAttribute("adminOtpVerified", true);
            auditLogService.logAction("ADMIN_OTP_VALIDATED");
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            return "admin/verify-otp";
        }
    }

    @GetMapping("/admin/verify-otp-resend")
    public String resendAdminOtp(Principal principal, HttpServletRequest request, Model model) {
        String username = principal.getName();
        User admin = userService.findByUsername(username);

        if (admin == null) {
            model.addAttribute("error", "Admin user not found.");
            return "error";
        }

        int newOtp = new Random().nextInt(900_000) + 100_000;
        HttpSession session = request.getSession();
        session.setAttribute("adminOtp", newOtp);

        long expiryMillis = System.currentTimeMillis() + (5 * 60 * 1000);
        session.setAttribute("adminOtpExpiry", expiryMillis);

        notificationService.sendEmail(admin.getEmail(), "Resent Admin OTP", "Your new OTP is: " + newOtp);

        auditLogService.logAction("ADMIN_OTP_RESENT");

        model.addAttribute("maskedEmail", maskEmail(admin.getEmail()));
        model.addAttribute("maskedPhone", maskPhone(admin.getPhone()));
        model.addAttribute("info", "New OTP sent.");
        return "admin/verify-otp";
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    private String maskPhone(String phone) {
        return "*******" + phone.substring(phone.length() - 3);
    }
}
