package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedRequest {

  private Long id;

  private Long bid;

  private String status;

  private List<String> containers = new ArrayList<>();

  private ContractRequest contract;
}
