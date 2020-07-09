package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
  
  private Long id;
  
  private String sender;
  
  private Long report;
  
  private String title;
  
  private String detail;
  
  private String status;

}
