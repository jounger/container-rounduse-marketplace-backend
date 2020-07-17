package com.crm.enums;

public enum EnumShippingLineNotification {
  
  REQUEST, CANCEL;
  
  public static EnumShippingLineNotification findByName(String name) {
    for (EnumShippingLineNotification type : EnumShippingLineNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
