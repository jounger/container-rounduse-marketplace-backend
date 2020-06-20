package com.crm.models.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.crm.models.Supplier;
import com.crm.models.dto.SupplierDto;

public class SupplierMapper {

	public static SupplierDto toSupplierDto(Supplier supplier) {
		SupplierDto supplierDto = new SupplierDto();
		supplierDto.setUsername(supplier.getUsername());
		
		Map<String, String> address = new HashMap<>();
		address = AddressMapper.toAddressHashMap(supplier.getAddress());
		supplierDto.setAddress(address);
		supplierDto.setEmail(supplier.getEmail());
		supplierDto.setPhone(supplier.getPhone());
		supplierDto.setStatus(supplier.getStatus().name());
		Set<String> roles = new HashSet<>();
		supplier.getRoles().forEach(role -> roles.add(role.getName()));
		supplierDto.setRoles(roles);
		supplierDto.setWebsite(supplier.getWebsite());
		supplierDto.setContactPerson(supplier.getContactPerson());
		supplierDto.setCompanyName(supplier.getCompanyName());
		return supplierDto;
	}
}
