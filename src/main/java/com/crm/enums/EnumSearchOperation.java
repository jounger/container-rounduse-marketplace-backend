package com.crm.enums;

public enum EnumSearchOperation {
  
  EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;

  public static final String[] SIMPLE_OPERATION_SET = { ":", "!", ">", "<", "~" };

  public static EnumSearchOperation getSimpleOperation(char input) {
    switch (input) {
    case ':':
      return EQUALITY;
    case '!':
      return NEGATION;
    case '>':
      return GREATER_THAN;
    case '<':
      return LESS_THAN;
    case '~':
      return LIKE;
    default:
      return null;
    }
  }
}
