package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.Loan;
import com.netbanking.netbanking_app.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    @Autowired
    private LoanService loanService;

    // ✅ HTML: Manager Dashboard Page
    @GetMapping("/dashboard")
    public String managerDashboard(Model model) {
        model.addAttribute("message", "Welcome to the Manager Dashboard!");
        return "manager-dashboard";  // Thymeleaf template
    }

    // ✅ HTML: View Loan Requests Page
    @GetMapping("/loan-requests")
    public String viewPendingLoansPage(Model model) {
        List<Loan> pendingLoans = loanService.getPendingLoans();
        model.addAttribute("pendingLoans", pendingLoans);
        return "manager-loan-requests"; // Thymeleaf template
    }

    // ✅ HTML: Handle Loan Approval (via form POST)
    @PostMapping("/approve-loan")
    public String approveLoanHtml(@RequestParam Long loanId) {
        loanService.approveLoan(loanId);
        return "redirect:/manager/loan-requests";
    }

    // ✅ HTML: Loan Report Page
    @GetMapping("/loan-report")
    public String loanReportPage(Model model) {
        List<Loan> allLoans = loanService.getAllLoans();
        model.addAttribute("loans", allLoans);
        return "manager-loan-report"; // Thymeleaf template
    }

    // ✅ API: Get All Pending Loans as JSON
    @GetMapping("/loans/pending")
    @ResponseBody
    public ResponseEntity<?> viewPendingLoansApi() {
        return ResponseEntity.ok(loanService.getPendingLoans());
    }

    // ✅ API: Approve Loan via JSON API (PUT)
    @PutMapping("/loans/approve")
    @ResponseBody
    public ResponseEntity<?> approveLoanApi(@RequestParam Long loanId) {
        return ResponseEntity.ok(loanService.approveLoan(loanId));
    }

    // ✅ API: Simple Loan Reports as JSON
    @GetMapping("/reports")
    @ResponseBody
    public ResponseEntity<?> generateReportsApi() {
        List<String> reports = List.of(
                "✅ Monthly Loan Approvals: 32",
                "✅ Total Disbursed: ₹22.5 Lakhs",
                "✅ Active Loans: 58"
        );
        return ResponseEntity.ok(reports);
    }
}
