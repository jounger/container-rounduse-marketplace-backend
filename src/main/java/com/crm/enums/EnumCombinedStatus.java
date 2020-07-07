package com.crm.enums;

public enum EnumCombinedStatus {

  INFO_RECEIVED, SHIPPING, DELIVERED, PAID, EXCEPTION;

  public static EnumCombinedStatus findByName(String name) {
    for (EnumCombinedStatus status : EnumCombinedStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    return null;
  }
}
