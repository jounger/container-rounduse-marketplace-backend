package com.crm.models.mapper;

import java.util.HashMap;
import java.util.Map;

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
		supplierDto.setRole(supplier.getRoles().iterator().next().toString());
		supplierDto.setWebsite(supplier.getWebsite());
		supplierDto.setContactPerson(supplier.getContactPerson());
		supplierDto.setCompanyName(supplier.getCompanyName());
		return supplierDto;
	}
}
