package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.dto.ChangePasswordRequest;
import com.netbanking.netbanking_app.dto.FundTransferRequest;
import com.netbanking.netbanking_app.dto.LoanRepaymentSummary;
import com.netbanking.netbanking_app.model.*;
import com.netbanking.netbanking_app.repository.BillRepository;
import com.netbanking.netbanking_app.repository.LoanRepository;
import com.netbanking.netbanking_app.repository.NotificationRepository;
import com.netbanking.netbanking_app.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BillRepository billRepository;


    @Autowired
    private LoanApplicationService loanApplicationService;


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanRepaymentService loanRepaymentService;


    @GetMapping("/dashboard")
    public String customerDashboard(Model model, Principal principal, HttpServletRequest request) {
        try {
            System.out.println("🚀 Dashboard accessed by: " + principal.getName());

            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                System.out.println("❌ User not found");
                return "redirect:/login?error=user-not-found";
            }

            List<LoanApplication> loans = loanApplicationService.getLoansByUserId(user.getUserId());
            List<Bill> bills = billRepository.findByUserIdAndStatus(user.getUserId(), "UNPAID");

            List<Loan> relevantLoans = loanRepository.findByUserIdAndStatusIn(user.getUserId(), List.of("ACTIVE", "APPROVED"));
            List<LoanRepaymentSummary> repaymentSummaries = new ArrayList<>();
            for (Loan loan : relevantLoans) {
                repaymentSummaries.add(loanRepaymentService.getRepaymentSummary(loan.getLoanId(), user.getUserId()));
            }
            model.addAttribute("repaymentSummaries", repaymentSummaries);

            List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
            if (accounts == null || accounts.isEmpty()) {
                System.out.println("⚠️ No accounts found for user ID: " + user.getUserId());
                model.addAttribute("user", user);
                model.addAttribute("accounts", new ArrayList<>());
                model.addAttribute("error", "No accounts found");
                return "customer-dashboard";
            }

            Account primaryAccount = accounts.get(0);
            List<Transaction> transactions = transactionService.getTransactions(primaryAccount.getAccountId());

            // ✅ Show popup only if triggered explicitly (e.g. after transaction)
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object msg = session.getAttribute("popupMessage");
                Object type = session.getAttribute("popupType");

                if (msg != null && type != null) {
                    model.addAttribute("popupMessage", msg);
                    model.addAttribute("popupType", type);
                    session.removeAttribute("popupMessage");
                    session.removeAttribute("popupType");
                }
            }

            model.addAttribute("user", user);
            model.addAttribute("account", primaryAccount);
            model.addAttribute("accounts", accounts);
            model.addAttribute("transactions", transactions);
            model.addAttribute("transferRequest", new FundTransferRequest());
            model.addAttribute("loans", loans);
            model.addAttribute("bills", bills);
            model.addAttribute("repaymentSummaries", repaymentSummaries);

            return "customer-dashboard";
        } catch (Exception e) {
            System.err.println("❌ Error in dashboard: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading the dashboard");
            return "error";
        }
    }


    @GetMapping("/profile")
    public String customerProfile(Model model, Principal principal, HttpSession session) {
        try {
            System.out.println("🚀 Profile accessed by: " + principal.getName());

            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                System.out.println("❌ User not found");
                return "redirect:/login?error=user-not-found";
            }

            session.setAttribute("userId", user.getUserId());

            List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
            List<LoanApplication> loans = loanApplicationService.getLoansByUserId(user.getUserId());
            List<Bill> bills = billRepository.findByUserIdAndStatus(user.getUserId(), "UNPAID");

            // ➕ Add this to populate repayment summaries
            List<Loan> relevantLoans = loanRepository.findByUserIdAndStatusIn(user.getUserId(), List.of("ACTIVE", "APPROVED"));
            List<LoanRepaymentSummary> repaymentSummaries = new ArrayList<>();
            for (Loan loan : relevantLoans) {
                repaymentSummaries.add(loanRepaymentService.getRepaymentSummary(loan.getLoanId(), user.getUserId()));
            }
            model.addAttribute("repaymentSummaries", repaymentSummaries);


            model.addAttribute("user", user);
            model.addAttribute("accounts", accounts);
            model.addAttribute("loans", loans);
            model.addAttribute("bills", bills);
            model.addAttribute("repaymentSummaries", repaymentSummaries); // ✅ Attach to model

            return "customer/profile-details";
        } catch (Exception e) {
            System.err.println("❌ Error in profile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading the profile");
            return "error";
        }
    }



    @GetMapping("/account/balance")
    @ResponseBody
    public ResponseEntity<BigDecimal> getAccountBalance(@RequestParam Long accountId) {
        try {
            BigDecimal balance = accountService.getBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            System.err.println("❌ Error getting balance: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "customer/edit-profile"; // must match your template name
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User user, Principal principal, HttpSession session) {
        User existingUser = userService.getUserByUsername(principal.getName());
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setDob(user.getDob());
        existingUser.setAddress(user.getAddress());
        existingUser.setCity(user.getCity());
        existingUser.setParentNum(user.getParentNum());

        userService.updateUser(existingUser);
        session.setAttribute("popupMessage", "✅ Profile updated successfully");
        session.setAttribute("popupType", "SUCCESS");

        return "redirect:/customer/profile";
    }

    @GetMapping("/profile/password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "customer/change-password"; // ✅ This is your new HTML view
    }

    @PostMapping("/profile/password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request,
                                 Principal principal,
                                 Model model,
                                 HttpSession session) {

        String username = principal.getName();
        boolean success = userService.changePassword(
                username,
                request.getOldPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );

        if (success) {
            // ✅ Show popup on dashboard after success
            session.setAttribute("popupMessage", "✅ Password changed successfully.");
            session.setAttribute("popupType", "SUCCESS");

            return "redirect:/customer/profile";
        } else {
            // ❌ Inline error on change-password page
            model.addAttribute("error", "❌ Password change failed. Please check your inputs.");
            return "customer/change-password";
        }
    }

    @GetMapping("/profile/json")
    @ResponseBody
    public ResponseEntity<User> getProfileAsJson(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


    @GetMapping("/transactions/json")
    @ResponseBody
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam Long accountId) {
        try {
            List<Transaction> transactions = transactionService.getTransactions(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            System.err.println("❌ Error fetching transactions: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/transactions/view")
    public String viewTransactions(@RequestParam("accountId") Long accountId,
                                   Model model,
                                   Principal principal) {
        try {
            System.out.println("📌 Transaction view triggered for account ID: " + accountId);

            if (principal == null || principal.getName() == null) {
                System.out.println("❌ Principal is missing or anonymous");
                model.addAttribute("error", "Login session expired or unauthorized.");
                return "error";
            }

            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                System.out.println("❌ User not found: " + principal.getName());
                model.addAttribute("error", "User not found.");
                return "error";
            }

            Account selectedAccount = accountService.getAccountById(accountId);
            if (selectedAccount == null) {
                System.out.println("❌ Account not found with ID: " + accountId);
                model.addAttribute("error", "Account not found.");
                return "error";
            }

            if (!selectedAccount.getUserId().equals(user.getUserId())) {
                System.out.println("⛔ Unauthorized access attempt by user ID: " + user.getUserId());
                model.addAttribute("error", "Access denied. Account does not belong to user.");
                return "error";
            }

            List<Transaction> transactions = transactionService.getTransactions(accountId);
            System.out.println("📈 Found " + transactions.size() + " transactions for account ID " + accountId);

            model.addAttribute("user", user);
            model.addAttribute("selectedAccount", selectedAccount);
            model.addAttribute("transactions", transactions);
            return "transaction-history";

        } catch (Exception e) {
            System.err.println("❌ Exception while viewing transactions: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unexpected error occurred while loading transactions.");
            return "error";
        }
    }

    @GetMapping("/notifications")
    public String viewNotifications(Model model, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal); // implement this method
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        model.addAttribute("notifications", notifications);
        return "customer/notifications"; // This refers to templates/customer/notifications.html
    }

    // ✅ JSON Debug endpoint for direct testing (Postman-friendly)
    @GetMapping("/transactions/view/debug")
    @ResponseBody
    public ResponseEntity<?> viewTransactionsDebug(@RequestParam("accountId") Long accountId,
                                                   Principal principal) {
        try {
            System.out.println("🔧 DEBUG view endpoint hit for account ID: " + accountId);

            User user = userService.getUserByUsername(principal.getName());
            Account account = accountService.getAccountById(accountId);

            if (account == null || user == null || !account.getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(403).body("Access denied or invalid data");
            }

            List<Transaction> transactions = transactionService.getTransactions(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            System.err.println("❌ Debug error: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/api/customer/transfer", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> transferFundsApi(@RequestBody FundTransferRequest transferRequest) {
        try {
            Transaction txn = fundTransferService.transfer(transferRequest);

            User user = userService.getUserById(transferRequest.getUserId());
            auditLogService.logAction(
                    "FUND_TRANSFER_API",
                    user.getUsername(),
                    user.getUserId(),
                    transferRequest.getToAccountNumber(),
                    "₹" + txn.getAmount() + " transferred via API from Acc#" + txn.getAccountId() + " to Acc#" + transferRequest.getToAccountNumber(),
                    false,
                    null
            );

            return ResponseEntity.ok(txn);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/test-notification-save")
    public ResponseEntity<String> testNotification() {
        Notification notification = new Notification();
        notification.setUserId(26L);
        notification.setMessage("🚨 Test notification from controller");
        notification.setNotificationType("INFO");
        notification.setPriority("HIGH");
        notification.setIsRead("N");
        notification.setSentAt(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());

        try {
            notificationRepository.save(notification);
            return ResponseEntity.ok("✅ Notification saved");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Save failed: " + e.getMessage());
        }
    }


    @PostMapping("/transfer")
    public String transferFunds(@ModelAttribute FundTransferRequest request,
                                Principal principal,
                                HttpSession session) {
        try {
            // 🔐 Validate authenticated user
            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                System.out.println("❌ Transfer failed: user not found");
                return "redirect:/customer/fund-transfer?error=user";
            }

            // 🏦 Set default 'from account' if not provided
            if (request.getFromAccountId() == null) {
                List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());
                if (accounts != null && !accounts.isEmpty()) {
                    request.setFromAccountId(accounts.get(0).getAccountId());
                } else {
                    System.out.println("❌ Transfer failed: no accounts found");
                    return "redirect:/customer/fund-transfer?error=noaccounts";
                }
            }

            // 🧠 Validate destination account number
            if (request.getToAccountNumber() == null || request.getToAccountNumber().trim().isEmpty()) {
                System.out.println("❌ Transfer failed: invalid destination account number");
                return "redirect:/customer/fund-transfer?error=invalidaccount";
            }

            // 💸 Perform transfer
            fundTransferService.transfer(request);

            // 📝 Audit log for transparency
            auditLogService.logAction(
                    "FUND_TRANSFER_UI",
                    user.getUsername(),
                    user.getUserId(),
                    request.getToAccountNumber(),
                    "₹" + request.getAmount() + " transferred via UI from Acc#" + request.getFromAccountId() + " to Acc#" + request.getToAccountNumber(),
                    false,
                    null
            );

            // ✅ Trigger dashboard popup
            session.setAttribute("popupMessage", "₹" + request.getAmount() + " transferred successfully to Acc#" + request.getToAccountNumber());
            session.setAttribute("popupType", "SUCCESS");

            return "redirect:/customer/dashboard";

        } catch (Exception e) {
            System.err.println("❌ Transfer failed: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/customer/fund-transfer?error=true";
        }
    }



    @GetMapping("/fund-transfer")
    public String showFundTransferForm(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("transferRequest", new FundTransferRequest());

        return "customer/fund-transfer"; // This view needs to be created
    }

    @GetMapping("/loan/repayment/initiate/{loanId}/{emiNumber}")
    public String showEmiPaymentPage(@PathVariable Long loanId,
                                     @PathVariable int emiNumber,
                                     Model model,
                                     Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        List<Account> accounts = accountService.getAccountsByUserId(user.getUserId());

        BigDecimal monthlyAmount = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getLoanDuration() * 12), 2, RoundingMode.HALF_UP);

        model.addAttribute("loan", loan);
        model.addAttribute("emiNumber", emiNumber);
        model.addAttribute("monthlyAmount", monthlyAmount);
        model.addAttribute("accounts", accounts);
        return "customer/emi-confirm";
    }

    @PostMapping("/loan/repayment/pay")
    public String processEmiPayment(@RequestParam Long loanId,
                                    @RequestParam int emiNumber,
                                    @RequestParam String paymentMode,
                                    @RequestParam Long fromAccountId,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session) {

        User user = userService.getUserByUsername(principal.getName());

        // 🏦 Get loan and EMI details
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        BigDecimal monthlyAmount = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getLoanDuration() * 12), 2, RoundingMode.HALF_UP);

        // 🔐 Validate balance before paying
        Account account = accountService.getAccountById(fromAccountId);
        if (account.getBalance().compareTo(monthlyAmount) < 0) {
            redirectAttributes.addFlashAttribute("paymentMessage", "❌ Payment failed: Insufficient balance in account");
            return "redirect:/customer/loan/repayment/success";
        }

        // 💳 Record EMI payment first
        String response = loanRepaymentService.payEmi(loanId, user.getUserId(), emiNumber, paymentMode);

        // 💸 Deduct amount from account
        BigDecimal updatedBalance = account.getBalance().subtract(monthlyAmount);
        account.setBalance(updatedBalance);
        accountService.saveAccount(account);

        // 🧾 Log it into transaction history
        Transaction txn = new Transaction();
        txn.setAccountId(fromAccountId);
        txn.setAmount(monthlyAmount);
        txn.setTransactionType("LOAN_EMI_PAYMENT");
        txn.setDescription("EMI #" + emiNumber + " paid for Loan ID " + loanId);
        txn.setStatus("COMPLETED");
        txn.setTransactionDate(LocalDateTime.now());

        transactionService.saveTransaction(txn);

        // ✅ Notify user on dashboard with popup
        session.setAttribute("popupMessage", "✅ EMI #" + emiNumber + " paid successfully for Loan ID #" + loanId);
        session.setAttribute("popupType", "SUCCESS");

        // ✅ Also show on repayment screen
        redirectAttributes.addFlashAttribute("paymentMessage", response);

        return "redirect:/customer/loan/repayment/success";
    }





    @PostMapping("/customer/fund-transfer")
    public String processFundTransfer(@ModelAttribute FundTransferRequest transferRequest,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        try {
            fundTransferService.transfer(transferRequest);

            // 🧑‍💼 Fetch authenticated user from session
            User user = userService.getUserByUsername(principal.getName());

            // 📝 Log successful fund transfer into audit_logs
            auditLogService.logAction(
                    "FUND_TRANSFER_UI",
                    user.getUsername(),
                    user.getUserId(),
                    transferRequest.getToAccountNumber(), // Optional: mask if needed
                    "₹" + transferRequest.getAmount() + " transferred via UI from Acc#" +
                            transferRequest.getFromAccountId() + " to Acc#" + transferRequest.getToAccountNumber(),
                    false,
                    null
            );

            redirectAttributes.addFlashAttribute("success", "Transfer successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: " + e.getMessage());
        }

        return "redirect:/customer/profile";
    }


    @GetMapping("/loan/repayment/success")
    public String showSuccessPage(Model model) {
        return "customer/emi-success";
    }


    private Long getUserIdFromPrincipal(Principal principal) {
        // You can improve this depending on your actual UserDetails implementation
        String username = principal.getName();
        return userService.getUserByUsername(username).getUserId(); // Fetch user ID by username
    }
}
