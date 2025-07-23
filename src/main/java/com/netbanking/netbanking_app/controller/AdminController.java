package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.dto.AdminAccountRequest;
import com.netbanking.netbanking_app.model.*;
import com.netbanking.netbanking_app.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BillPaymentService billPaymentService;


    @Autowired
    private LoanService loanService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AccountService accountService;

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "******";
        int atIndex = email.indexOf("@");
        return "****" + email.substring(atIndex);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 3) return "*******";
        return "*******" + phone.substring(phone.length() - 3);
    }


    // 🔒 Optional API route (renamed to avoid POST conflict with form)
    @PostMapping("/api/create-account")
    public ResponseEntity<Account> createAccountViaApi(@RequestBody AdminAccountRequest request) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setAccountNumber("ACC" + System.currentTimeMillis());

        account.setInterestRate(request.getInterestRate());
        account.setBranchCode(request.getBranchCode());
        account.setIfscCode(request.getIfscCode());
        account.setBranchName(request.getBranchName());
        account.setStatus(request.getStatus());
        account.setMinBalance(request.getMinBalance());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        Account created = accountService.createAccount(account);
        return ResponseEntity.ok(created);
    }

    // 🌐 Admin Dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    // 💳 Card Listing + Filtering
    @GetMapping("/cards")
    public String showCardDashboard(@RequestParam(name = "status", required = false, defaultValue = "ALL") String status, Model model) {
        List<Card> cards = switch (status.toUpperCase()) {
            case "ACTIVE" -> cardService.getCardsByStatus("ACTIVE");
            case "BLOCKED" -> cardService.getCardsByStatus("BLOCKED");
            default -> cardService.getAllCards();
        };
        model.addAttribute("cards", cards);
        return "admin/card-dashboard";
    }

    // 💳 Issue New Card
    @GetMapping("/cards/issue")
    public String showCardIssueForm(Model model) {
        model.addAttribute("card", new Card());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/card-issue";
    }

    @PostMapping("/cards/issue")
    public String issueCard(@ModelAttribute("card") Card card) {
        cardService.addCard(card);
        return "redirect:/admin/cards";
    }

    // 🔒 Card Status Updates
    @GetMapping("/cards/block/{cardId}")
    public String blockCard(@PathVariable Long cardId) {
        Card card = cardService.getCardById(cardId);
        if (card != null) {
            card.setStatus("BLOCKED");
            cardService.addCard(card);
        }
        return "redirect:/admin/cards";
    }

    @GetMapping("/cards/unblock/{cardId}")
    public String unblockCard(@PathVariable Long cardId) {
        cardService.updateCardStatus(cardId, "ACTIVE");
        return "redirect:/admin/cards?status=BLOCKED";
    }

    // 📝 Loans Panel
    @GetMapping("/loans")
    public String showLoanRequests(@RequestParam(name = "status", defaultValue = "ALL") String status, Model model) {
        List<Loan> loans = status.equalsIgnoreCase("ALL")
                ? loanService.getAllRequests()
                : loanService.getRequestsByStatus(status);
        model.addAttribute("loans", loans);
        return "admin/loan-requests";
    }

    @PostMapping("/loans/update")
    public String updateLoanStatus(@RequestParam Long loanId, @RequestParam String action) {
        loanService.updateLoanStatus(loanId, action);
        return "redirect:/admin/loans";
    }

    // 👥 User Management + KYC
    @GetMapping("/users")
    public String showUserList(Model model) {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            user.setAccounts(accountService.getAccountsByUserId(user.getUserId()));
            user.setCards(cardService.getCardsByUserId(user.getUserId()));
            user.setLoans(loanService.getLoansByUserId(user.getUserId()));
            user.setBills(billPaymentService.getBillsByUserId(user.getUserId()));

        }
        model.addAttribute("users", users);
        return "admin/user-management";
    }

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute("user") User user) {
        user.setRegistrationDate(LocalDate.now());
        user.setIsActive("Y");

        if (user.getKycStatus() == null) {
            user.setKycStatus("PENDING");
        }

        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserActiveStatus(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/kyc/{userId}")
    public String updateKycStatus(@PathVariable Long userId, @RequestParam String status) {
        userService.updateKycStatus(userId, status);
        return "redirect:/admin/users";
    }

    // 📋 View System Logs
    @GetMapping("/logs")
    public String viewAuditLogs(Model model) {
        List<AuditLog> logs = auditLogService.getAllLogs();
        model.addAttribute("logs", logs);
        return "admin/logs";
    }

    // 🏦 Account Creation (Form-based)
    @GetMapping("/create-account")
    public String showCreateAccountForm(Model model) {
        model.addAttribute("adminAccountRequest", new AdminAccountRequest());
        return "admin/create-account";
    }

    @PostMapping("/create-account")
    public String createAccountFromForm(@ModelAttribute AdminAccountRequest request, Model model) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setInterestRate(request.getInterestRate());
        account.setBranchCode(request.getBranchCode());
        account.setIfscCode(request.getIfscCode());
        account.setBranchName(request.getBranchName());
        account.setStatus(request.getStatus());
        account.setMinBalance(request.getMinBalance());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setAccountNumber("" + System.currentTimeMillis());

        accountService.createAccount(account);
        model.addAttribute("message", "✅ Account created successfully for user ID " + account.getUserId());

        return "admin/create-account";
    }

    @GetMapping("/accounts")
    public String viewAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "admin/account-dashboard";
    }





}
