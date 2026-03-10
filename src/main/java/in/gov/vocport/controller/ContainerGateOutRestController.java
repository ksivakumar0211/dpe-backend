package in.gov.vocport.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
}