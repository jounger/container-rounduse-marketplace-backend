package com.crm.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierControllerIT {

  private String s = "";
  
  @BeforeEach
  public void setUp() {
    s = "123";
  }
  
  @Test
  void test() {
    assertThat(s).isNotEqualToIgnoringCase("321");
  }

}
