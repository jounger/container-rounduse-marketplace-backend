package com.crm.enums;

public enum EnumQRTokenStatus {
  SHIPPING, DELIVERED;

  public static EnumQRTokenStatus findByName(String name) {
    for (EnumQRTokenStatus status : EnumQRTokenStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return null;
  }
}
