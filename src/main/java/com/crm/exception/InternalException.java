package com.crm.exception;

public class InternalException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public InternalException(String message) {
    super(message);
  }

}
