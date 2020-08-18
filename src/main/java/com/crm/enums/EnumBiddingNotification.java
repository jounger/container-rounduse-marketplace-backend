package com.crm.enums;

public enum EnumBiddingNotification {

  BIDDING_INVITED, BIDDING_CANCELED, BIDDING_EDITED, BID_ADDED, BID_REJECTED, BID_ACCEPTED, BID_EDITED, CONTRACT_ADD,
  CONTRACT_ACCEPTED, CONTRACT_REJECTED, CONTRACT_EDITED;

  public static EnumBiddingNotification findByName(String name) {
    for (EnumBiddingNotification type : EnumBiddingNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
