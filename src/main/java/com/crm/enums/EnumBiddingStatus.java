package com.crm.enums;

public enum EnumBiddingStatus {
  
  BIDDING, COMBINED, CANCELED, EXPIRED;

  public static EnumBiddingStatus findByName(String name) {
    for (EnumBiddingStatus status : EnumBiddingStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return null;
  }
}
