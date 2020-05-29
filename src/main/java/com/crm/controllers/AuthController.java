package com.crm.controllers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.enums.EnumRole;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.SignInRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.payload.response.JwtResponse;
import com.crm.payload.response.MessageResponse;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.UserService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private RoleRepository roleRepository;
  
  @Autowired
  private UserService userService;

  @Autowired
  private JwtUntils jwtUntils;
  
  @GetMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest request) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUntils.generateJwtToken(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(role -> role.getAuthority())
        .collect(Collectors.toList());
    
    JwtResponse response = new JwtResponse();
    response.setToken(jwt);
    response.setId(userDetails.getId());
    response.setUsername(userDetails.getUsername());
    response.setFullname(userDetails.getFullname());
    response.setEmail(userDetails.getEmail());
    response.setRoles(roles);
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
    if(userRepository.existsByUsername(request.getUsername()) || 
        userRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: User has been existed"));
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(request.getPassword());
    user.setEmail(request.getEmail());
    user.setFullname(request.getFullname());
    
    Set<String> rolesString = request.getRoles();
    Set<Role> roles = new HashSet<>();
    List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());
    
    if(rolesString == null) {
      Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        for(int i = 0; i < rolesEnum.size(); i++) {
          if(role.equalsIgnoreCase(rolesEnum.get(i).name().split("_")[1])) {
            Role userRole = roleRepository.findByName(rolesEnum.get(i))
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
          }
        }
      });
    }
    user.setRoles(roles);
    userService.saveUser(user);
    
    return ResponseEntity.ok(new MessageResponse("User registered succesfully"));
  }
  
}
