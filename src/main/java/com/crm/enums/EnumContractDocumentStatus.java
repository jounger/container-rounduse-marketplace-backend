package com.crm.enums;

public enum EnumContractDocumentStatus {

  PENDING, ACCEPTED, REJECTED;

  public static EnumContractDocumentStatus findByName(String name) {
    for (EnumContractDocumentStatus type : EnumContractDocumentStatus.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
