package in.gov.vocport.client;

import in.gov.vocport.exception.PosApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class PosBridgeClient {

    private final RestTemplate restTemplate;

    @Value("${razorpay.pos.base-url}")
    private String baseUrl;

    @Value("${razorpay.pos.app-key}")
    private String appKey;

    @Value("${razorpay.pos.username}")
    private String username;

    @Value("${razorpay.pos.device-id}")
    private String deviceId;

    public PosBridgeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /* ---------------- PAY API ---------------- */
    public Map<String, Object> initiatePayment(String orderId, double amount) {
        return callApi("/pay", buildPayRequest(orderId, amount));
    }

    /* ---------------- STATUS API ---------------- */
    public Map<String, Object> getStatus(String p2pRequestId) {

        Map<String, Object> request = new HashMap<>();
        request.put("appKey", appKey);
        request.put("username", username);
        request.put("origP2pRequestId", p2pRequestId);

        return callApi("/status", request);
    }

    /* ---------------- CANCEL API ---------------- */
    public void cancelPayment(String p2pRequestId) {

        Map<String, Object> request = new HashMap<>();
        request.put("appKey", appKey);
        request.put("username", username);
        request.put("origP2pRequestId", p2pRequestId);
        request.put("pushTo", Map.of("deviceId", deviceId));

        callApi("/cancel", request);
    }

    /* ---------------- COMMON API HANDLER ---------------- */
    @SuppressWarnings("rawtypes")
	private Map<String, Object> callApi(String path, Map<String, Object> body) {
    	System.out.println(body);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(baseUrl + path, entity, Map.class);

            @SuppressWarnings("unchecked")
			Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new PosApiException("Empty response from POS server");
            }

            if (Boolean.FALSE.equals(responseBody.get("success"))) {
                throw new PosApiException(
                        (String) responseBody.get("errorCode"),
                        (String) responseBody.get("errorMessage")
                );
            }

            return responseBody;

        } catch (HttpStatusCodeException ex) {
            throw new PosApiException(
                    "HTTP_" + ex.getStatusCode().value(),
                    ex.getResponseBodyAsString()
            );
        } catch (ResourceAccessException ex) {
            throw new PosApiException("POS server unreachable");
        }
    }

    private Map<String, Object> buildPayRequest(String orderId, double amount) {
        Map<String, Object> request = new HashMap<>();
        request.put("username", username.toString());
        request.put("appKey", appKey.toString());
//        request.put("amount", String.valueOf(amount));
        request.put("externalRefNumber", orderId.toString());
        request.put("amount", BigDecimal.valueOf(amount));
        request.put("pushTo", Map.of("deviceId", deviceId.toString()));
        request.put("mode", "CARD");
        return request;
    }
}
