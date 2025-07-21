package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.dto.LoanRepaymentSummary;

import java.util.List;

public interface LoanRepaymentService {
    LoanRepaymentSummary getRepaymentSummary(Long loanId, Long userId);

    List<LoanRepaymentSummary> getAllRepaymentSummaries(Long userId);

    String payEmi(Long loanId, Long userId, int emiNumber, String paymentMode);

}
