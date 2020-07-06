package com.crm.enums;

public enum EnumCurrency {

  VND, USD;

  public static EnumCurrency findByName(String name) {
    for (EnumCurrency currency : EnumCurrency.values()) {
      if (currency.name().equalsIgnoreCase(name)) {
        return currency;
      }
    }

    return null;
  }
}
