package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ShippingLineService;

@Service
public class ShippingLineServiceImpl implements ShippingLineService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ShippingLineRepository shippingLineRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void saveShippingLine(ShippingLineRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		ShippingLine shippingLine = new ShippingLine();
		shippingLine.setUsername(request.getUsername());
		shippingLine.setEmail(request.getEmail());
		shippingLine.setPhone(request.getPhone());
		shippingLine.setStatus(EnumUserStatus.ACTIVE.name());
		shippingLine.setWebsite(request.getWebsite());
		shippingLine.setCompanyName(request.getCompanyName());
		shippingLine.setCompanyName(request.getCompanyCode());
		Role userRole = roleRepository.findByName("ROLE_SHIPPINGLINE")
				.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
		shippingLine.getRoles().add(userRole);
		String address = request.getAddress();
		if (address != null) {
			shippingLine.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		shippingLine.setPassword(encoder);
		shippingLineRepository.save(shippingLine);
	}

}
