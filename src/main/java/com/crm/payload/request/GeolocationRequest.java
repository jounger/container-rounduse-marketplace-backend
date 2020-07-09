package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeolocationRequest {

  private Long id;

  private String latitude;

  private String longitude;
}
