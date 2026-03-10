package in.gov.vocport.controller;

import in.gov.vocport.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public Map<String, Object> pay(
            @PathVariable String orderId,
            @RequestParam double amount) {

        return paymentService.processPayment(orderId, amount);
    }
}

