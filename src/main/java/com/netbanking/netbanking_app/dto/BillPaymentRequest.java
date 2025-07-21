package com.netbanking.netbanking_app.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillPaymentRequest {
    private Long billId;
    private Long accountId;
    private BigDecimal amount;
    private String paymentMethod; // e.g., ONLINE, UPI, CARD
}
