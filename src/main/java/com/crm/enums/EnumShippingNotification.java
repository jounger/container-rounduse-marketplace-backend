package com.crm.enums;

public enum EnumShippingNotification {

  TASK, CANCEL, UPDATED;

  public static EnumShippingNotification findByName(String name) {
    for (EnumShippingNotification type : EnumShippingNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
