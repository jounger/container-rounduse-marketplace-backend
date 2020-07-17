package com.crm.enums;

public enum EnumDriverNotification {

  TASK, CANCEL;

  public static EnumDriverNotification findByName(String name) {
    for (EnumDriverNotification type : EnumDriverNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
