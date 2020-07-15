package com.crm.enums;

public enum EnumPaymentType {
  
  FINES, PAYMENT;
  
  public static EnumPaymentType findByName(String name) {
    for (EnumPaymentType type : EnumPaymentType.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
