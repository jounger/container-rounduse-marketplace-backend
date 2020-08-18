package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeolocationRequest {

  private Long id;

  private String latitude;

  private String longitude;
}
