package in.gov.vocport.service;

import in.gov.vocport.client.PosBridgeClient;
import in.gov.vocport.exception.PaymentProcessingException;
import in.gov.vocport.repository.DirectPortServiceChargeHeaderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private final PosBridgeClient posBridgeClient;
    private final DirectPortServiceChargeHeaderRepository headerRepository;
    private final DirectPortServiceChargeService portServiceChargeService;

    public PaymentService(PosBridgeClient posBridgeClient, DirectPortServiceChargeHeaderRepository headerRepository, DirectPortServiceChargeService portServiceChargeService) {
        this.posBridgeClient = posBridgeClient;
        this.headerRepository = headerRepository;
        this.portServiceChargeService = portServiceChargeService;
    }

    //Flow need to modified

    public Map<String, Object> processPayment(String chitNo, double amount, List<String> cfsNoList) {
        String orderId = headerRepository.findRefTransNo("PO", LocalDate.now());

        Map<String, Object> payResponse =
                posBridgeClient.initiatePayment(orderId, amount);

        String p2pRequestId = (String) payResponse.get("p2pRequestId");

        sleep(30);

        for (int i = 0; i < 12; i++) {

            Map<String, Object> status =
                    posBridgeClient.getStatus(p2pRequestId);

            String messageCode = (String) status.get("messageCode");
            String paymentStatus = (String) status.get("status");

            if ("P2P_DEVICE_TXN_DONE".equals(messageCode)) {

                if ("AUTHORIZED".equalsIgnoreCase(paymentStatus)) {
                    Map<String, Object> resultMap = Map.of(
                            "status", "SUCCESS",
                            "p2pRequestId", p2pRequestId
                    );
                    portServiceChargeService.updatePaymentInfo(orderId, cfsNoList, chitNo, resultMap);
                    return resultMap;
                }

                if ("FAILED".equalsIgnoreCase(paymentStatus)) {
                    throw new PaymentProcessingException(
                            "Payment failed on POS device"
                    );
                }
            }

            if ("P2P_DEVICE_CANCELED".equals(messageCode)) {
                throw new PaymentProcessingException(
                        "Payment cancelled on POS device"
                );
            }

            sleep(10);
        }

        posBridgeClient.cancelPayment(p2pRequestId);

        throw new PaymentProcessingException(
                "Payment timed out after 150 seconds"
        );
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ignored) {
        }
    }
}
