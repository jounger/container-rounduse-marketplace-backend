package com.crm.enums;

public enum EnumReportStatus {
  
  PENDING, RESOLVED, REJECTED, UPDATED, CLOSED;

  public static EnumReportStatus findByName(String name) {
    for (EnumReportStatus status : EnumReportStatus.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    return null;
  }
}
