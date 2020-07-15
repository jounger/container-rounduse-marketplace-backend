package com.crm.enums;

public enum EnumReportNotificationType {

  NEW, FEEDBACK, UPDATE;

  public static EnumReportNotificationType findByName(String name) {
    for (EnumReportNotificationType status : EnumReportNotificationType.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return null;
  }
}
