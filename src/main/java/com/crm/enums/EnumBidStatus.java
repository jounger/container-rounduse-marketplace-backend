package com.crm.enums;

public enum EnumBidStatus {
	PENDING,
	ACCEPTED,
	REJECTED,
	EXPIRED,
	CANCELED;
	
	public static EnumBidStatus findByName(String name) {
		for(EnumBidStatus status : EnumBidStatus.values()) {
			if(status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		
		return null;
	}
}
