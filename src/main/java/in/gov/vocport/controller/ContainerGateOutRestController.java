package in.gov.vocport.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import in.gov.vocport.dto.ContainerGateInDto;
import in.gov.vocport.entities.ContainerGateInEntity;
import in.gov.vocport.service.ContainerGateOutService;
import jakarta.validation.Valid;

@RestController
public class ContainerGateOutRestController {
	
	@Autowired
	private ContainerGateOutService service;

	@PostMapping("/gateOut")
	public ResponseEntity<?> saveGateOut(@Valid @RequestBody ContainerGateInDto dto) {

//		if (bindingResult.hasErrors()) {
//			Map<String, String> errors = new HashMap<>();
//			bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
//			Map<String, Object> resp = new HashMap<>();
//			resp.put("success", false);
//			resp.put("errors", errors);
//			return ResponseEntity.badRequest().body(resp);
//		}

		String userCode = "SYSTEM";
		try {
			userCode = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ignored) {
		}

		ContainerGateInEntity saved = service.saveOrUpdate(dto, userCode);
		Map<String, Object> resp = new HashMap<>();
		resp.put("success", saved);
		resp.put("message", "Gate Out saved successfully");
		resp.put("chitNo", saved.getChit_no());
		return ResponseEntity.ok(resp);
	}
	
	@PostMapping("/gateOutUpdate")
	public ResponseEntity<?> saveGateOutUpdate(@Valid @RequestBody ContainerGateInDto dto) {

//		if (bindingResult.hasErrors()) {
//			Map<String, String> errors = new HashMap<>();
//			bindingResult.getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
//			Map<String, Object> resp = new HashMap<>();
//			resp.put("success", false);
//			resp.put("errors", errors);
//			return ResponseEntity.badRequest().body(resp);
//		}

		String userCode = "SYSTEM";
		try {
			userCode = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ignored) {
		}

		ContainerGateInEntity saved = service.saveOrUpdate(dto, userCode);
		Map<String, Object> resp = new HashMap<>();
		resp.put("success", saved);
		resp.put("message", "Gate Out Data Updated successfully");
		resp.put("chitNo", saved.getChit_no());
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/getout-payment-status")
	public ResponseEntity getOutPaymentStatus(@RequestParam String containerNo) {
		Map<String, Object> result = new HashMap<>();
		service.getOutPaymentStatus(containerNo, result);
		return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
	}

	@GetMapping("/getout-leo-status")
	public ResponseEntity getOutLeoStatus(@RequestParam String containerNo) {
		Map<String, Object> result = new HashMap<>();
		service.getOutLeoStatus(containerNo, result);
		return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
	}

	@GetMapping("/get-container")
	public ResponseEntity getAllContainer(@RequestParam(required = false) String containerNo) {
		Map<String, Object> result = new HashMap<>();
		service.getAllContainer(containerNo, result);
		return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
	}

	@GetMapping("/container-details")
	public ResponseEntity getContainerDetails(@RequestParam String containerNo) {
		Map<String, Object> result = new HashMap<>();
		service.getContainerDetails(containerNo, result);
		return result.containsKey("error") ? new ResponseEntity<>(result,  HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result,  HttpStatus.OK);
	}
}