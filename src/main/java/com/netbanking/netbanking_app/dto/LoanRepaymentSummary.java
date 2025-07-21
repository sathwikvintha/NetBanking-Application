package com.netbanking.netbanking_app.dto;

import com.netbanking.netbanking_app.model.LoanRepaymentTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoanRepaymentSummary {
    private Long loanId;
    private int emisPaid;
    private int totalEmis;
    private BigDecimal remainingPrincipal;
    private BigDecimal estimatedInterest;
    private List<LoanRepaymentTransaction> transactions;

    // ✅ Added for approval status checks in UI
    private String loanStatus;
}
