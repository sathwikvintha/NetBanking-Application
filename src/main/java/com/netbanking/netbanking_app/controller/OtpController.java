package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.UserService;
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
public class OtpController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/customer/verify-otp")
    public String verifyOtpPage(Principal principal, HttpServletRequest request, Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error";
        }

        int otp = new Random().nextInt(900_000) + 100_000;
        HttpSession session = request.getSession();
        session.setAttribute("otp", otp);

        long expiryMillis = System.currentTimeMillis() + (5 * 60 * 1000);
        session.setAttribute("otpExpiry", expiryMillis);

        notificationService.sendEmail(user.getEmail(), "Your NetBanking Login OTP", "Your OTP is: " + otp + "\nValid for 5 minutes only.");

        model.addAttribute("maskedEmail", maskEmail(user.getEmail()));
        model.addAttribute("maskedPhone", maskPhone(user.getPhone()));
        return "customer/verify-otp";
    }

    @PostMapping("/customer/verify-otp-submit")
    public String validateOtp(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Object expectedOtpObj = session.getAttribute("otp");
        Object expiryObj = session.getAttribute("otpExpiry");

        if (expectedOtpObj == null || expiryObj == null) {
            model.addAttribute("error", "OTP expired. Please request a new one.");
            return "customer/verify-otp";
        }

        int expectedOtp = (int) expectedOtpObj;
        long expiryTime = (long) expiryObj;
        long currentTime = System.currentTimeMillis();

        if (currentTime > expiryTime) {
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiry");
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            return "customer/verify-otp";
        }

        String enteredStr = request.getParameter("enteredOtp");
        if (enteredStr == null || !enteredStr.matches("\\d{6}")) {
            model.addAttribute("error", "Please enter a valid 6-digit OTP.");
            return "customer/verify-otp";
        }

        int enteredOtp = Integer.parseInt(enteredStr);

        if (enteredOtp == expectedOtp) {
            session.removeAttribute("otp");
            session.removeAttribute("otpExpiry");
            session.setAttribute("otpVerified", true);
            return "redirect:/customer/dashboard";
        } else {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            return "customer/verify-otp";
        }
    }

    @GetMapping("/customer/verify-otp-resend")
    public String resendOtp(Principal principal, HttpServletRequest request, Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error";
        }

        int newOtp = new Random().nextInt(900_000) + 100_000;
        HttpSession session = request.getSession();
        session.setAttribute("otp", newOtp);

        long expiryMillis = System.currentTimeMillis() + (5 * 60 * 1000);
        session.setAttribute("otpExpiry", expiryMillis);

        notificationService.sendEmail(user.getEmail(), "Your NetBanking Login OTP", "Your new OTP is: " + newOtp);

        model.addAttribute("maskedEmail", maskEmail(user.getEmail()));
        model.addAttribute("maskedPhone", maskPhone(user.getPhone()));
        model.addAttribute("info", "New OTP sent.");
        return "customer/verify-otp";
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    private String maskPhone(String phone) {
        return "*******" + phone.substring(phone.length() - 3);
    }
}
