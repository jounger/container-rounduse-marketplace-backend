package com.crm.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Merchant;
import com.crm.models.Role;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.MerchantRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.MerchantService;

@Service
public class MerchantServiceImpl implements MerchantService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void saveMerchant(SupplierRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		Merchant merchant = new Merchant();
		merchant.setUsername(request.getUsername());
		merchant.setEmail(request.getEmail());
		merchant.setPhone(request.getPhone());
		merchant.setStatus(EnumUserStatus.PENDING);
		merchant.setWebsite(request.getWebsite());
		merchant.setCompanyName(request.getCompanyName());
		merchant.setShortName(request.getShortName());
		merchant.setDescription(request.getDescription());
		merchant.setTin(request.getTin());
		merchant.setFax(request.getFax());
//		merchant.setContact(request.getContact());
//		merchant.setBankAccount(request.getBankAccount());
		Set<String> rolesString = request.getRoles();
		Set<Role> roles = new HashSet<>();
		List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());
		if (rolesString == null) {
			Role userRole = roleRepository.findByName(EnumRole.ROLE_FORWARDER)
					.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
			roles.add(userRole);
		} else {
			rolesString.forEach(role -> {
				for (int i = 0; i < rolesEnum.size(); i++) {
					if (role.equalsIgnoreCase(rolesEnum.get(i).name().split("_")[1])) {
						Role userRole = roleRepository.findByName(rolesEnum.get(i))
								.orElseThrow(() -> new NotFoundException("Error: Role is not found"));
						roles.add(userRole);
					}
				}
			});
		}
		merchant.setRoles(roles);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			merchant.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		merchant.setPassword(encoder);
		
		merchantRepository.save(merchant);
		
	}

}
