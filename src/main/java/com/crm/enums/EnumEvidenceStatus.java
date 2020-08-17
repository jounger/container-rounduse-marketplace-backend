package com.crm.enums;

public enum EnumEvidenceStatus {

  PENDING, ACCEPTED, REJECTED;

  public static EnumEvidenceStatus findByName(String name) {
    for (EnumEvidenceStatus type : EnumEvidenceStatus.values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
