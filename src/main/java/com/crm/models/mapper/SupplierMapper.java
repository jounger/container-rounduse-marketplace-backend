package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Supplier;
import com.crm.models.dto.SupplierDto;

public class SupplierMapper {

	public static SupplierDto toSupplierDto(Supplier supplier) {
		SupplierDto supplierDto = new SupplierDto();
		supplierDto.setId(supplier.getId());
		supplierDto.setUsername(supplier.getUsername());
		supplierDto.setAddress(supplier.getAddress());
		supplierDto.setEmail(supplier.getEmail());
		supplierDto.setPhone(supplier.getPhone());
		supplierDto.setStatus(supplier.getStatus());
		
		Set<String> supplierRoles = new HashSet<>();
		supplier.getRoles().forEach(role -> supplierRoles.add(RoleMapper.toRoleDto(role).getName()));
		supplierDto.setRoles(supplierRoles);
		
		supplierDto.setWebsite(supplier.getWebsite());
		supplierDto.setContactPerson(supplier.getContactPerson());
		supplierDto.setCompanyName(supplier.getCompanyName());
		return supplierDto;
	}
}
