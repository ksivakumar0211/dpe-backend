package in.gov.vocport.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.gov.vocport.dto.AuthUserDto;
import in.gov.vocport.dto.GateInRowDto;
import in.gov.vocport.dto.GateInSearchCriteria;
import in.gov.vocport.dto.GateOutSearchCriteria;
import in.gov.vocport.dto.MenuItemDto;
import in.gov.vocport.dto.PagedResponse;
import in.gov.vocport.dto.UserListDto;
import in.gov.vocport.dto.gateOutRowDto;
import in.gov.vocport.entities.ContainerGateInEntity;
import in.gov.vocport.repository.CommonSearchOptionRepository;
import in.gov.vocport.repository.ContainerGateInRepository;
import in.gov.vocport.service.ContainerGateInService;
import in.gov.vocport.service.ContainerGateOutService;
import in.gov.vocport.service.MenuService;
import in.gov.vocport.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PageController {

	@Autowired
	private MenuService menuService;

	@Autowired
	private UserService userService;

	@Autowired
	private ContainerGateInService gateInService;
	
	@Autowired
	private ContainerGateOutService gateOutService;

	@Autowired
	private ContainerGateInRepository containerGateInRepository;

	@Autowired
	private CommonSearchOptionRepository commonSearchOptionRepository;

	private List<MenuItemDto> getMenu() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof AuthUserDto) {
				userId = ((AuthUserDto) principal).getUserId();
			} else if (principal instanceof String) {
				userId = (String) principal;
			} else {
				userId = authentication.getName();
			}
		}
		List<MenuItemDto> tree = menuService.getMenuForUser(userId);
		return tree;
	}

	@GetMapping("/menubyuser/{userId}")
	public ResponseEntity getMenuByUserId(@PathVariable String userId) {
		userId = userId.equals("null") ? null : userId.replace('-', '/');
		Map<String, Object> result = new HashMap<>();
		List<MenuItemDto> tree = getMenu(userId);
		result.put("success", tree);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private List<MenuItemDto> getMenu(String userId) {
		return menuService.getMenuForUser(userId);
	}

	@GetMapping("/getAllUser")
	public ResponseEntity addUserAccess() {
		Map<String, Object> result = new HashMap<>();
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
//			List<MenuItemDto> tree = getMenu();
			result.put("users", users);
//			result.put("menus", tree);
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (SQLException e) {
			return new ResponseEntity<>(result.put("error", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getAllEditUser")
	public ResponseEntity editUserAccess() {
		Map<String,Object> result = new HashMap<>();
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforEditUserAccess();
//			List<MenuItemDto> tree = getMenu();
//			result.put("menu", tree);
			result.put("users", users);
			return new ResponseEntity<>(result,HttpStatus.OK);
		} catch (SQLException e) {
			return new ResponseEntity<>(result.put("error",e.getMessage()),HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/addGateIn")
	public ResponseEntity addGateIn() {
		Map<String, Object> result = Map.of("chitNo", gateInService.getNewChitNo(),
				"inTime", gateInService.getCurrentInTime());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/editGateIn")
	public ResponseEntity editGateIn(@RequestBody GateInSearchCriteria criteria,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {
		Map<String, Object> result = new HashMap<>();
		boolean hasSearchParams = isNotBlank(criteria.getChitNo()) || isNotBlank(criteria.getContainerNo())
				|| isNotBlank(criteria.getVehicleNo()) || criteria.getGateInDate() != null;
		if (!hasSearchParams) {
			result.put("error", "Error Occurred in Gate In Search");
		} else {
			PagedResponse<GateInRowDto> resp = gateInService.searchGateIn(criteria, page, size);
			result.put("success", resp);
		}
		return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

	private boolean isNotBlank(String s) {
		return s != null && !s.trim().isEmpty();
	}

	@GetMapping("/editGateInPage")
	public ResponseEntity editGateInPage(@RequestParam("chitNo") String chitNo) {
		ContainerGateInEntity gateIn = containerGateInRepository.findById(chitNo)
				.orElseThrow(() -> new IllegalArgumentException("Invalid chit no"));

		String toLocName = commonSearchOptionRepository.findLocationNameByCode(gateIn.getTo_loc_id());
		String chAgentName = commonSearchOptionRepository.findAgentNameByCode(gateIn.getParty_cd());
		String portName = commonSearchOptionRepository.findPortNameByCode(gateIn.getTrn_shp_port());
		String linerName = commonSearchOptionRepository.findLinerNameByCode(gateIn.getSical_line_code());
		String cargoName = commonSearchOptionRepository.findCargoNameByCode(gateIn.getCargo_code());
		String formLocation = commonSearchOptionRepository.findLocationNameByCode(gateIn.getFrom_loc_id());
		String vesselName = commonSearchOptionRepository.findVesselNameByCode(gateIn.getVessel_no());
//		Map<String, Object> result = Map.of("locationName", toLocName == null ? "" : toLocName,
//				"agentName", chAgentName == null ? "" : chAgentName,
//				"portName",portName == null ? "" : portName,
//				"linerName", linerName == null ? "" : linerName,
//				"cargoName", cargoName == null ? "" : cargoName);

		Map<String, Object> result = Map.of("success", gateIn,
				"locationName", toLocName == null ? "" : toLocName,
				"agentName", chAgentName == null ? "" : chAgentName,
				"portName",portName == null ? "" : portName,
				"linerName", linerName == null ? "" : linerName,
				"cargoName", cargoName == null ? "" : cargoName,
				"formLocationName", formLocation == null ? "" : formLocation,
				"vesselName", vesselName == null ? "" : vesselName);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/addGateOut")
	public ResponseEntity addGateOut() {
		Map<String, Object> result = Map.of("chitNo", gateOutService.getNewChitNo(),
				"inTime", gateOutService.getCurrentInTime());

		return new ResponseEntity<>(result, HttpStatus.OK);
//
//		model.addAttribute("chitNo", gateOutService.getNewChitNo());
//		model.addAttribute("inTime", gateOutService.getCurrentInTime());
	}

	@PostMapping("/editGateOut")
	public ResponseEntity editGateOut(@RequestBody GateOutSearchCriteria criteria,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {
		Map<String, Object> result = new HashMap<>();
//		List<UserListDto> users = null;
//		try {
//			users = userService.getAllUsersforEditUserAccess();
//		} catch (SQLException e) {
//		}
//		List<MenuItemDto> tree = getMenu();
//		model.addAttribute("menu", tree);
//		model.addAttribute("users", users);
//		model.addAttribute("currentPath", request.getRequestURI());
		boolean hasSearchParams = isNotBlank(criteria.getChitNo()) || isNotBlank(criteria.getContainerNo())
				|| isNotBlank(criteria.getVehicleNo()) || criteria.getGateOutDate() != null;

		if (!hasSearchParams) {
			result.put("error", "Error Occurred in Gate Out Search");
		} else {
			PagedResponse<gateOutRowDto> resp = gateOutService.searchGateOut(criteria, page, size);
			result.put("success", resp);
		}
		return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/editGateOutPage")
	public ResponseEntity editGateOutPage(@RequestParam("chitNo") String chitNo) {
//		List<MenuItemDto> tree = getMenu();
//		model.addAttribute("menu", tree);
//		model.addAttribute("users", users);
//		model.addAttribute("currentPath", request.getRequestURI());
		ContainerGateInEntity gateIn = containerGateInRepository.findById(chitNo)
				.orElseThrow(() -> new IllegalArgumentException("Invalid chit no"));
//		model.addAttribute("gateIn", gateIn);
		String fromLocName = commonSearchOptionRepository.findLocationNameByCode(gateIn.getFrom_loc_id());
		String toLocName = commonSearchOptionRepository.findLocationNameByCode(gateIn.getTo_loc_id());
		String chAgentName = commonSearchOptionRepository.findAgentNameByCode(gateIn.getParty_cd());
		String portName = commonSearchOptionRepository.findPortNameByCode(gateIn.getTrn_shp_port());
		String linerName = commonSearchOptionRepository.findLinerNameByCode(gateIn.getSical_line_code());
		String cargoName = commonSearchOptionRepository.findCargoNameByCode(gateIn.getCargo_code());
//		model.addAttribute("fromLocationName", fromLocName);
//		model.addAttribute("toLocationName", toLocName);
//		model.addAttribute("chAgentName", chAgentName);
//		model.addAttribute("portOfDestinationName", portName);
//		model.addAttribute("linerNameDisplay", linerName);
//		model.addAttribute("cargoName", cargoName);
//		model.addAttribute("vesselNameDisplay", gateIn.getSical_vessel_name());
//		model.addAttribute("shipperNameDisplay", gateIn.getShipper());
//		return "gate_out_edit_page";
		Map<String, Object> result = Map.of("success", gateIn,
				"fromLocationName", fromLocName == null ? "" : fromLocName,
				"locationName", toLocName == null ? "" : toLocName,
				"agentName", chAgentName == null ? "" : chAgentName,
				"portName",portName == null ? "" : portName,
				"linerName", linerName == null ? "" : linerName,
				"cargoName", cargoName == null ? "" : cargoName);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/addDpeServiceCharge")
	public String addDpeServiceCharge(HttpServletRequest request, Model model, Principal principal) {
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
		} catch (SQLException e) {
		}
		List<MenuItemDto> tree = getMenu();
		model.addAttribute("menu", tree);
		model.addAttribute("users", users);
		model.addAttribute("currentPath", request.getRequestURI());
		return "add_dpe_service_charge";
	}
	
	@GetMapping("/editDpeServiceCharge")
	public String editDpeServiceCharge(HttpServletRequest request, Model model, Principal principal) {
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
		} catch (SQLException e) {
		}
		List<MenuItemDto> tree = getMenu();
		model.addAttribute("menu", tree);
		model.addAttribute("users", users);
		model.addAttribute("currentPath", request.getRequestURI());
		return "edit_dpe_service_charge";
	}
	
	@GetMapping("/addDocUpload")
	public String addDocUpload(HttpServletRequest request, Model model, Principal principal) {
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
		} catch (SQLException e) {
		}
		List<MenuItemDto> tree = getMenu();
		model.addAttribute("menu", tree);
		model.addAttribute("users", users);
		model.addAttribute("currentPath", request.getRequestURI());
		return "under_construction";
	}
	
	@GetMapping("/editDocUpload")
	public String editDocUpload(HttpServletRequest request, Model model, Principal principal) {
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
		} catch (SQLException e) {
		}
		List<MenuItemDto> tree = getMenu();
		model.addAttribute("menu", tree);
		model.addAttribute("users", users);
		model.addAttribute("currentPath", request.getRequestURI());
		return "under_construction";
	}
	
	@GetMapping("/viewDocUpload")
	public String viewDocUpload(HttpServletRequest request, Model model, Principal principal) {
		List<UserListDto> users = null;
		try {
			users = userService.getAllUsersforAddUserAccess();
		} catch (SQLException e) {
		}
		List<MenuItemDto> tree = getMenu();
		model.addAttribute("menu", tree);
		model.addAttribute("users", users);
		model.addAttribute("currentPath", request.getRequestURI());
		return "under_construction";
	}

	@GetMapping("/get/fromLocation")
	public ResponseEntity getVesselsNo() {
		Map<String, Object> result = new HashMap<>();
		gateInService.getFromLocation(result);
		return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
	}
}