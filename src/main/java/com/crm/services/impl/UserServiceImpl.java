package com.crm.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.ChangeUserStatusRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void saveUser(SignUpRequest request) {
		if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
				|| userRepository.existsByPhone(request.getPhone())) {
			throw new DuplicateRecordException("Error: User has been existed");
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setStatus(EnumUserStatus.PENDING);
		Set<String> rolesString = request.getRoles();
		Set<Role> roles = new HashSet<>();
		List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());

		if (rolesString == null) {
			Role userRole = roleRepository.findByName(EnumRole.ROLE_OTHER)
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
		user.setRoles(roles);
		Address address = (Address) request.getAddress();
		if (address == null) {
			throw new NotFoundException("Error: Address is not found");
		} else {
			user.setAddress(address);
		}
		String encoder = passwordEncoder.encode(request.getPassword());
		user.setPassword(encoder);
		userRepository.save(user);
	}

	@Override
	public Page<User> getUsers(PaginationRequest request) {
		Page<User> pages = null;
		if(request.getStatus() == null) {
			pages = userRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
		}else {
			pages = userRepository.	findByStatus(EnumUserStatus.findByName(request.getStatus()), PageRequest.of(request.getPage(), request.getLimit()))	;	
		}
		return pages;
	}

	@Override
	public void changeStatus(ChangeUserStatusRequest request) {

		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new NotFoundException("Error: User is not found"));
		EnumUserStatus status = EnumUserStatus.findByName(request.getStatus());
		if (status.equals(null)) {
			throw new NotFoundException("Error: Status is not found");
		}
		user.setStatus(status);
		userRepository.save(user);
	}

	@Override
	public void getUsersByRoleName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInfomation(SignUpRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new NotFoundException("User is not found."));

	}
}
