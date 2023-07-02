package app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.service.LoginService;
import app.service.RegisterService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

	@Autowired
	private RegisterService registerService;
	@Autowired
	private LoginService loginService;

	@PostMapping("/register/{username}")
	public ResponseEntity<?> register(@PathVariable String username) {
		try {
			// Call registration service
			boolean registrationStatus = registerService.registerUser(username);
			
			// Return response (true if registration successful)
	        if (registrationStatus){
	            return new ResponseEntity<>(HttpStatus.CREATED);
	        } else{
	            return new ResponseEntity<>("Failed to create user.", HttpStatus.BAD_REQUEST);
	        }
		} catch (Exception e) {
			String errorMessage = "Failed to create user: " + e.getMessage();
			return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/login/{username}")
	public ResponseEntity<?> authenticate(@PathVariable String username) {
		try {
			// Call login service
			boolean authenticationStatus = loginService.authenticateUser(username);

			// Return response (true if authentication successful)
			if (authenticationStatus){
	            return new ResponseEntity<>(HttpStatus.OK);
	        } else{
	            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	        }
		} catch (Exception e) {
			String errorMessage = "Login Process Failed: " + e.getMessage();
			return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}