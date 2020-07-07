package com.crm.enums;

public enum EnumTrailerType {
  
  T28, T32, T34, T36, T40, T45, T48;
  
  public static EnumTrailerType findByName(String name) {
    for (EnumTrailerType type : EnumTrailerType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
