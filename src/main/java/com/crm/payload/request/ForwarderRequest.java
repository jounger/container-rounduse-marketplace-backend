package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForwarderRequest extends SupplierRequest{
	
	private String contact;
	
	private String bankAccount;
}
