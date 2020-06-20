package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Operator;
import com.crm.models.Role;
import com.crm.payload.request.OperatorRequest;
import com.crm.repository.OperatorRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.OperatorService;

@Service
public class OperatorServiceImpl implements OperatorService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OperatorRepository operatorRepository;

	@Override
	public void saveOperator(OperatorRequest request) {

		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		Operator operator = new Operator();
		operator.setUsername(request.getUsername());
		operator.setEmail(request.getEmail());
		operator.setPhone(request.getPhone());
		operator.setStatus(EnumUserStatus.ACTIVE);
		Role userRole = roleRepository.findByName("ROLE_MODERATOR")
				.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
		operator.getRoles().add(userRole);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			operator.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		operator.setPassword(encoder);
		operatorRepository.save(operator);

	}

}
