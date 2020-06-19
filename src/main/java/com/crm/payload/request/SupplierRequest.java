package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest extends SignUpRequest{

	private String website;
	
	private String contactPerson;
	
	private String companyName;

	private String companyCode;
	
	private String companyDescription;
	
	private String companyAddress;
	
	private String tin;
	
	private String fax;
}
