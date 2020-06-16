package com.crm.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.dto.UserDto;
import com.crm.payload.request.SignInRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.payload.request.SystemAdminRequest;
import com.crm.payload.response.JwtResponse;
import com.crm.payload.response.MessageResponse;
import com.crm.security.jwt.AuthTokenFilter;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsImpl;
import com.crm.security.services.UserDetailsServiceImpl;
import com.crm.services.ForwarderService;
import com.crm.services.MerchantService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private ForwarderService forwarderService;

	@Autowired
	private JwtUntils jwtUntils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
		logger.info(request.getUsername());
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUntils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		Set<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority())
				.collect(Collectors.toSet());
		logger.info("JWT: {}", jwt);

		UserDto userInfo = new UserDto();
		userInfo.setUsername(userDetails.getUsername());
		userInfo.setPhone(userDetails.getPhone());
		userInfo.setRoles(roles);
		userInfo.setEmail(userDetails.getEmail());
		userInfo.setStatus(userDetails.getStatus());
		
		JwtResponse response = new JwtResponse();
		response.setIdToken(jwt);
		response.setUserInfo(userInfo);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Access-Control-Expose-Headers", "Authorization");
		responseHeaders.set("Authorization", "Bearer " + jwt);
		return ResponseEntity.ok().headers(responseHeaders).body(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SupplierRequest request) {
		String role = request.getRoles().iterator().next();
		switch (role.toLowerCase()) {
		case "forwarder":
			forwarderService.saveForwarder(request);
			break;
		case "merchant":
			merchantService.saveMerchant(request);
		default:
			break;
		}

		return ResponseEntity.ok(new MessageResponse("User registered succesfully"));
	}

	@GetMapping("/refresh")
	public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			UsernamePasswordAuthenticationToken authentication = authUserByToken(request);
			if (authentication != null) {
				String jwtRefresh = jwtUntils.generateJwtToken(authentication);
				JwtResponse responseJwt = new JwtResponse();
				responseJwt.setIdToken(jwtRefresh);

				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set("Access-Control-Expose-Headers", "Authorization");
				responseHeaders.set("Authorization", "Bearer " + jwtRefresh);
				return ResponseEntity.ok().headers(responseHeaders).body(responseJwt);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Cannot authenticate this JWT"));
	}

	@GetMapping("/user")
	public ResponseEntity<?> fetchUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			UsernamePasswordAuthenticationToken authentication = authUserByToken(request);
			if (authentication != null) {
				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

				Set<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority())
						.collect(Collectors.toSet());

				UserDto userInfo = new UserDto();
				userInfo.setUsername(userDetails.getUsername());
				userInfo.setPhone(userDetails.getPhone());
				userInfo.setRoles(roles);
				userInfo.setEmail(userDetails.getEmail());

				JwtResponse responseJwt = new JwtResponse();
				responseJwt.setUserInfo(userInfo);

				return ResponseEntity.ok().body(responseJwt);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Cannot authenticate this JWT"));
	}

	private UsernamePasswordAuthenticationToken authUserByToken(HttpServletRequest request)
			throws ServletException, IOException {
		try {
			logger.info("doFilterInternal {}", request.getHeader("Authorization"));
			String jwt = AuthTokenFilter.parseJwt(request);
			if (jwt != null && jwtUntils.validateJwtToken(jwt) != null) {
				String username = jwtUntils.getUsernameFromJwtToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				return authentication;
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}
		return null;
	}

}
