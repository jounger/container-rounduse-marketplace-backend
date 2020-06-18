package com.crm.enums;

public enum EnumUserStatus {
	PENDING,
	ACTIVE,
	BANNED;
	
	public static EnumUserStatus findByName(String name) {
		for(EnumUserStatus status : EnumUserStatus.values()) {
			if(status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		
		return null;
	}
}
