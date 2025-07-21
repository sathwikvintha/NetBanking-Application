package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.LoanApplication;
import com.netbanking.netbanking_app.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/customer/loans")
public class LoanController {

    @Autowired
    private LoanApplicationService loanService;

    @GetMapping("/apply")
    public String showLoanForm(Model model) {
        model.addAttribute("loanApplication", new LoanApplication());
        return "customer/loan-form";
    }

    @PostMapping("/apply")
    public String submitLoan(@ModelAttribute("loanApplication") @Valid LoanApplication loanApplication, Principal principal) {
        // Hardcode or fetch userId from Principal if available
        loanService.applyLoan(loanApplication);
        return "redirect:/customer/loans/status";
    }

    @GetMapping("/status")
    public String viewLoanStatus(Model model, Principal principal) {
        List<LoanApplication> loans = loanService.getLoansByUserId(1L); // Replace with real userId
        model.addAttribute("loans", loans);
        return "customer/loan-status";
    }
}
