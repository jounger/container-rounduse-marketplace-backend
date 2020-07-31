package com.crm.enums;

public enum EnumShippingStatus {

  INFO_RECEIVED, SHIPPING, DELIVERED, EXCEPTION;

  public static EnumShippingStatus findByName(String name) {
    for (EnumShippingStatus status : EnumShippingStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    return null;
  }
}
