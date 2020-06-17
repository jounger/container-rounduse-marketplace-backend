package com.crm.enums;

public enum EnumCurrency {
	VND,
	USD;
	
	public static EnumCurrency findByName(String name) {
		for(EnumCurrency status : EnumCurrency.values()) {
			if(status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		
		return null;
	}
}
