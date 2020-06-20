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
		merchant.setCompanyCode(request.getCompanyCode());
		merchant.setCompanyDescription(request.getCompanyDescription());
		merchant.setContactPerson(request.getContactPerson());
		merchant.setTin(request.getTin());
		merchant.setFax(request.getFax());
		Set<Role> roles = new HashSet<>();
		Role userRole = roleRepository.findByName("ROLE_MERCHANT")
            .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
        roles.add(userRole);
		merchant.setRoles(roles);
		Address address = (Address) request.getAddress();
		if (address != null) {
			merchant.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		merchant.setPassword(encoder);

		merchantRepository.save(merchant);
	}

}
