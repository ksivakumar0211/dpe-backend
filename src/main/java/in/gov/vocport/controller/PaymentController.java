package in.gov.vocport.controller;

import in.gov.vocport.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pos")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Map<String, Object>> pay(
            @RequestParam String chitNo,
            @RequestParam double amount,
            @RequestBody List<String> cfsNo) {

        return ResponseEntity.ok(paymentService.processPayment(chitNo, amount, cfsNo));
    }
}

