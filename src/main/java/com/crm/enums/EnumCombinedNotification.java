package com.crm.enums;

public enum EnumCombinedNotification {

  REQUEST, CANCEL, CONTRACT_ADD, CONTRACT_ACCEPTED, CONTRACT_REJECTED, CONTRACT_EDITED, INVOICE_ADD, INVOICE_ACCEPTED,
  INVOICE_REJECTED, INVOICE_EDITED;

  public static EnumCombinedNotification findByName(String name) {
    for (EnumCombinedNotification type : EnumCombinedNotification.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
