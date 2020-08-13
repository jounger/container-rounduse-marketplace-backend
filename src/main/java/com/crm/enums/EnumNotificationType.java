package com.crm.enums;

public enum EnumNotificationType {
  REPORT, BIDDING, SHPIPPING_LINE, DRIVER;

  public static EnumNotificationType findByName(String name) {
    for (EnumNotificationType type : EnumNotificationType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
