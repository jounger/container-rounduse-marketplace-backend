package com.crm.enums;

public enum EnumSupplyStatus {

	CREATED,
	PUBLISHED,
	BIDDING,
	COMBINED;
	
	public static EnumSupplyStatus findByName(String name) {
		for(EnumSupplyStatus status : EnumSupplyStatus.values()) {
			if(status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		
		return null;
	}
}
