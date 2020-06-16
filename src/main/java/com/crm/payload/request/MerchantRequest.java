package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantRequest extends SupplierRequest{
	
	private String contact;
	
	private String bankAccount;

}
