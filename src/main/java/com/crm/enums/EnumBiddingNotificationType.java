package com.crm.enums;

public enum EnumBiddingNotificationType {

  ADDED, REMOVED, MODIFIED, REJECTED, ACCEPTED;

  public static EnumBiddingNotificationType findByName(String name) {
    for (EnumBiddingNotificationType type : EnumBiddingNotificationType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
