package com.crm.enums;

public enum EnumSupplyStatus {

  CREATED, BIDDING, COMBINED, DONE;

  public static EnumSupplyStatus findByName(String name) {
    for (EnumSupplyStatus status : EnumSupplyStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    return null;
  }
}
