package com.crm.enums;

public enum EnumUnit {

  KG,
  KM;
  
  public static EnumUnit findByName(String name) {
      for(EnumUnit status : EnumUnit.values()) {
          if(status.name().equalsIgnoreCase(name)) {
              return status;
          }
      }
      
      return null;
  }
  
}
