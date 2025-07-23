package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.dto.BillPaymentRequest;
import com.netbanking.netbanking_app.model.Bill;
import com.netbanking.netbanking_app.model.Payment;

import java.util.List;

public interface BillPaymentService {
    Payment payBill(BillPaymentRequest request);
    List<Bill> getBillsByUserId(Long userId);
}
