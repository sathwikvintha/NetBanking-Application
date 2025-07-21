package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.dto.BillPaymentRequest;
import com.netbanking.netbanking_app.model.Payment;

public interface BillPaymentService {
    Payment payBill(BillPaymentRequest request);
}
