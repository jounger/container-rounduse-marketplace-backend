package com.crm.enums;

public enum EnumInvoiceType {
  
  FINES, PAYMENT;
  
  public static EnumInvoiceType findByName(String name) {
    for (EnumInvoiceType type : EnumInvoiceType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
