package com.crm.enums;

public enum EnumReportNotification {

  NEW, FEEDBACK, UPDATE, RESOLVED, REJECTED, CLOSED;

  public static EnumReportNotification findByName(String name) {
    for (EnumReportNotification status : EnumReportNotification.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return null;
  }
}
