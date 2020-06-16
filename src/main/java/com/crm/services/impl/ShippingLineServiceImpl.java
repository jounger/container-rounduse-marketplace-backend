package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Icd;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.repository.IcdRepository;
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
	private IcdRepository icdRepository;
	
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
		shippingLine.setStatus(EnumUserStatus.APPROVED);
		shippingLine.setWebsite(request.getWebsite());
		shippingLine.setName(request.getName());
		shippingLine.setShortName(request.getShortName());		
		Role userRole = roleRepository.findByName(EnumRole.ROLE_SHIPPINGLINE)
				.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
		shippingLine.getRoles().add(userRole);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			shippingLine.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		request.getIcdNameList().forEach(icdName -> {
			Icd icd = icdRepository.findByName(icdName)
					.orElseThrow(() -> new NotFoundException("ICD is not found."));
			shippingLine.getIcdList().add(icd);
		});
		shippingLine.setPassword(encoder);
		shippingLineRepository.save(shippingLine);
	}

}
