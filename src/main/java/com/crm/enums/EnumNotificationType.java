package com.crm.enums;

public enum EnumNotificationType {
  REPORT, BIDDING, SHIPPING, COMBINED;

  public static EnumNotificationType findByName(String name) {
    for (EnumNotificationType type : EnumNotificationType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
