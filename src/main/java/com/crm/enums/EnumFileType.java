package com.crm.enums;

public enum EnumFileType {
  IMAGE, DOCUMENT;

  public static EnumFileType findByName(String name) {
    for (EnumFileType type : EnumFileType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
