package com.crm.enums;

public enum EnumDriverNotificationType {

  TASK, CANCEL;

  public static EnumDriverNotificationType findByName(String name) {
    for (EnumDriverNotificationType type : EnumDriverNotificationType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
