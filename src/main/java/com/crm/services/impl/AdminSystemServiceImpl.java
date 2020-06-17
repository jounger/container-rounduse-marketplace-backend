package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Role;
import com.crm.models.SystemAdmin;
import com.crm.payload.request.SystemAdminRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.SystemAdminRepository;
import com.crm.repository.UserRepository;
import com.crm.services.AdminSystemService;

@Service
public class AdminSystemServiceImpl implements AdminSystemService{
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SystemAdminRepository systemAdminRepository;

	@Override
	public void saveAdmin(SystemAdminRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		SystemAdmin admin = new SystemAdmin();
		admin.setUsername(request.getUsername());
		admin.setEmail(request.getEmail());
		admin.setPhone(request.getPhone());
		admin.setStatus(EnumUserStatus.APPROVED);
		Role userRole = roleRepository.findByName(EnumRole.ROLE_ADMIN)
				.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
		admin.getRoles().add(userRole);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			admin.setAddress(address);
		}
		admin.setName(request.getName());
		admin.setRootUser(false);
		String encoder = passwordEncoder.encode(request.getPassword());
		admin.setPassword(encoder);
		systemAdminRepository.save(admin);
		
	}
	
	

}
