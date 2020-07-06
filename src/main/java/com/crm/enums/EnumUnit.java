package com.crm.enums;

public enum EnumUnit {
  
  S, KG, M, M3, FT, FT3;

  public static EnumUnit findByName(String name) {
    for (EnumUnit unit : EnumUnit.values()) {
      if (unit.name().equalsIgnoreCase(name)) {
        return unit;
      }
    }

    return null;
  }

}
