package in.gov.vocport.controller;

import in.gov.vocport.entities.CtThDirectServiceChg;
import in.gov.vocport.service.DirectPortServiceChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/service/charge")
@RequiredArgsConstructor
public class DirectPortServiceChargeController {
    private final DirectPortServiceChargeService service;

    @PostMapping("/add-edit")
    public ResponseEntity addEditServiceCharge(@RequestBody CtThDirectServiceChg ctThDirectServiceChg, @RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        service.addServiceCharge(ctThDirectServiceChg, userId, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity searchServiceCharge(@RequestParam String chitNo, @RequestParam String containerNo) {
        Map<String, Object> result = new HashMap<>();
        service.searchServiceCharge(chitNo, containerNo, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : ResponseEntity.ok(result);
    }

    @GetMapping("/pay-status-check")
    public ResponseEntity payStatusCheck(@RequestParam String cfsNo) {
        Map<String, Object> result = new HashMap<>();
        service.payStatusCheck(cfsNo, result);
        return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
    }

    @GetMapping("/delivery-date")
    public ResponseEntity getDeliveryDate(@RequestParam String admissionChitNo) {
        Map<String, Object> result = new HashMap<>();
        service.getDeliveryDate(admissionChitNo, result);
        return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
    }
}
