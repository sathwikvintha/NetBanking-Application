package com.netbanking.netbanking_app.controller;

import com.netbanking.netbanking_app.dto.BillPaymentRequest;
import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.Bill;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.repository.BillRepository;
import com.netbanking.netbanking_app.service.BillPaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer/bills")
public class BillPaymentController {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BillPaymentService billPaymentService;

    @GetMapping("/list")
    public String showBills(Model model, Principal principal) {
        Long userId = 1L; // Replace with actual user from principal
        List<Bill> bills = billRepository.findByUserIdAndStatus(userId, "UNPAID");
        model.addAttribute("bills", bills);
        return "customer/bill-list";
    }

    @GetMapping("/all/json")
    @ResponseBody
    public ResponseEntity<List<Bill>> getAllBillsJson(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId"); // or fetch via Principal if needed
        List<Bill> bills = billRepository.findByUserId(userId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/json/unpaid")
    @ResponseBody
    public ResponseEntity<List<Bill>> getUnpaidBills(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Bill> unpaid = billRepository.findByUserIdAndStatus(userId, "UNPAID");
        return ResponseEntity.ok(unpaid);
    }

    @GetMapping("/pay/{billId}")
    public String showPaymentForm(@PathVariable Long billId, Model model) {
        Bill bill = billRepository.findById(billId).orElseThrow();
        List<Account> accounts = accountRepository.findByUserId(bill.getUserId());
        model.addAttribute("bill", bill);
        model.addAttribute("accounts", accounts);
        model.addAttribute("paymentRequest", new BillPaymentRequest());
        return "customer/pay-bill";
    }

    @PostMapping("/pay")
    public String processPayment(@ModelAttribute BillPaymentRequest paymentRequest) {
        billPaymentService.payBill(paymentRequest);
        return "redirect:/customer/bills/list?success";
    }
}
