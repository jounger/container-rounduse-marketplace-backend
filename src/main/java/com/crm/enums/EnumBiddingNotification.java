package com.crm.enums;

public enum EnumBiddingNotification {

  ADDED, REMOVED, MODIFIED, REJECTED, ACCEPTED;

  public static EnumBiddingNotification findByName(String name) {
    for (EnumBiddingNotification type : EnumBiddingNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
