package com.crm.enums;

public enum EnumShippingLineNotificationType {
  
  REQUEST, CANCEL;
  
  public static EnumShippingLineNotificationType findByName(String name) {
    for (EnumShippingLineNotificationType type : EnumShippingLineNotificationType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
