package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.dto.LoanRepaymentSummary;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.LoanRepaymentService;
import com.netbanking.netbanking_app.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/loan/repayment")
public class LoanRepaymentController {

    @Autowired
    private LoanRepaymentService loanRepaymentService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @GetMapping("/summary/{loanId}")
    public ResponseEntity<LoanRepaymentSummary> getSummary(@PathVariable Long loanId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return ResponseEntity.ok(loanRepaymentService.getRepaymentSummary(loanId, userId));
    }

    @PostMapping("/pay/{loanId}/{emiNumber}")
    public ResponseEntity<String> payEmi(@PathVariable Long loanId,
                                         @PathVariable int emiNumber,
                                         @RequestParam String paymentMode,
                                         HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        // Perform EMI payment
        String resultMessage = loanRepaymentService.payEmi(loanId, userId, emiNumber, paymentMode);

        // Extract current user details
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId);

        // Log audit trail
        auditLogService.logAction(
                "EMI_PAID",
                currentUsername,
                userId,
                user != null ? user.getUsername() : "—",
                "EMI #" + emiNumber + " paid for Loan ID " + loanId + " using " + paymentMode,
                true,
                "Your EMI payment for Loan #" + loanId + " has been received."
        );

        return ResponseEntity.ok(resultMessage);
    }
}
