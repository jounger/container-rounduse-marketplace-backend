package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto extends UserDto{
	
	private String website;
	
	private String contactPerson;
	
	private String companyName;
	
	private String companyCode;
	
	private String companyDescription;
	
	private String companyAddress;
	
	private String tin;
	
	private String fax;
}
