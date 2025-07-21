package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.model.LoanApplication;
import com.netbanking.netbanking_app.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanRestController {

    @Autowired
    private LoanApplicationService loanService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyLoan(@RequestBody LoanApplication loanApplication) {
        loanService.applyLoan(loanApplication);
        return ResponseEntity.ok("Loan application submitted successfully");
    }
}

