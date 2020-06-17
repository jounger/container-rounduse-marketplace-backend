package com.crm.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Role;
import com.crm.payload.request.DriverRequest;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.DriverService;

@Service
public class DriverServiceImpl implements DriverService{

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ForwarderRepository forwarderRepository;

	@Override
	public void saveDriver(DriverRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		Driver driver = new Driver();
		driver.setUsername(request.getUsername());
		driver.setPhone(request.getPhone());
		driver.setEmail(request.getEmail());
		driver.setStatus(EnumUserStatus.PENDING);
		Set<String> rolesString = request.getRoles();
		Set<Role> roles = new HashSet<Role>();
		if (rolesString == null) {
			Role userRole = roleRepository.findByName("ROLE_DRIVER")
					.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
			roles.add(userRole);
		} else {
			rolesString.forEach(role -> {
						Role userRole = roleRepository.findByName(role)
								.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
						roles.add(userRole);
				});
		}
		driver.setRoles(roles);
		Forwarder forwarder =  forwarderRepository.findByUsername(request.getForwarderUsername())
				.orElseThrow(() -> new NotFoundException("Forwarder is not found"));
		forwarder.getDrivers().add(driver);
		forwarderRepository.save(forwarder);
	}

	@Override
	public void updateDriver(DriverRequest request) {

	}

	@Override
	public void deleteDriver(String username) {
		driverRepository.deleteByUsername(username);

	}

}
