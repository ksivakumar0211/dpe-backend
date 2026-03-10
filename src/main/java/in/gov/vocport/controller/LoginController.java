package in.gov.vocport.controller;

import in.gov.vocport.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity showWelcomePage(@RequestBody HashMap<String, String> userDetails) {
		// If already logged in, go to /home instead of login
//		if (authentication != null && authentication.isAuthenticated()
//				&& !(authentication instanceof AnonymousAuthenticationToken)) {
//			return "redirect:/home";
//		}
//		return "welcome";

		HashMap<String, Object> result = new HashMap<>();
		try {
			userService.loginValidate(userDetails, result);
			if (result.containsKey("error")) {
				return new ResponseEntity<>(result.get("error"), HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception exception) {
			return new ResponseEntity<>(result.put("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
