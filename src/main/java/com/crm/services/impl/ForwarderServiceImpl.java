package com.crm.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Forwarder;
import com.crm.models.Role;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ForwarderService;

@Service
public class ForwarderServiceImpl implements ForwarderService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ForwarderRepository forwarderRepository;

	@Override
	public void saveForwarder(SupplierRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		Forwarder forwarder = new Forwarder();
		forwarder.setUsername(request.getUsername());
		forwarder.setEmail(request.getEmail());
		forwarder.setPhone(request.getPhone());
		forwarder.setStatus(EnumUserStatus.PENDING);
		forwarder.setWebsite(request.getWebsite());
		forwarder.setCompanyName(request.getCompanyName());
		forwarder.setShortName(request.getShortName());
		forwarder.setDescription(request.getDescription());
		forwarder.setTin(request.getTin());
		forwarder.setFax(request.getFax());
//		forwarder.setContact(request.getContact());
//		forwarder.setBankAccount(request.getBankAccount());
		Set<String> rolesString = request.getRoles();
		Set<Role> roles = new HashSet<>();
//		List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());
		if (rolesString == null) {
			Role userRole = roleRepository.findByName("ROLE_FORWARDER")
					.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
			roles.add(userRole);
		} else {
			rolesString.forEach(role -> {
//				for (int i = 0; i < rolesEnum.size(); i++) {
//					if (role.equalsIgnoreCase(rolesEnum.get(i).name().split("_")[1])) {
//						Role userRole = roleRepository.findByName(rolesEnum.get(i))
			            Role userRole = roleRepository.findByName(role)
								.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
						roles.add(userRole);
//					}
//				}
			});
		}
		forwarder.setRoles(roles);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			forwarder.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		forwarder.setPassword(encoder);

		forwarderRepository.save(forwarder);

	}

}