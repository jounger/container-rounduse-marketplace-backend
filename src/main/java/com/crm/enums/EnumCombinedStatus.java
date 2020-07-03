package com.crm.enums;

public enum EnumCombinedStatus {

  INFO_RECEIVED, ON_THE_ROAD, SHIPPING, DELIVERED, PAID;

  public static EnumCombinedStatus findByName(String name) {
    for (EnumCombinedStatus status : EnumCombinedStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    return null;
  }
}
