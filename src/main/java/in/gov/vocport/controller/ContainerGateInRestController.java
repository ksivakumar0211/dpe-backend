package in.gov.vocport.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import in.gov.vocport.dto.ContainerGateInDto;
import in.gov.vocport.entities.ContainerGateInEntity;
import in.gov.vocport.service.ContainerGateInService;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ContainerGateInRestController {
	
	@Autowired
	private ContainerGateInService service;

	@PostMapping("/gatein")
	public ResponseEntity<?> saveGateIn(@Valid @RequestBody ContainerGateInDto dto) {
		String userCode = "SYSTEM";
		try {
			userCode = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ignored) {
		}

		ContainerGateInEntity saved = service.saveOrUpdate(dto, userCode);
		Map<String, Object> resp = new HashMap<>();
		resp.put("success", true);
		resp.put("message", "Gate In saved successfully");
		resp.put("chitNo", saved.getChit_no());
		return ResponseEntity.ok(resp);
	}
	
	@PostMapping("/gateinUpdate")
	public ResponseEntity<?> saveGateInUpdate(@Valid @RequestBody ContainerGateInDto dto) {

		String userCode = "SYSTEM";
		try {
			userCode = SecurityContextHolder.getContext().getAuthentication().getName();
		} catch (Exception ignored) {
		}

		ContainerGateInEntity saved = service.saveOrUpdate(dto, userCode);
		Map<String, Object> resp = new HashMap<>();
		resp.put("success", saved);
		resp.put("message", "Gate In Data Updated successfully");
		resp.put("chitNo", saved.getChit_no());
		return ResponseEntity.ok(resp);
	}
}