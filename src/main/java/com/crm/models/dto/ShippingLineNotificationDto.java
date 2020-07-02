package com.crm.models.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingLineNotificationDto extends NotificationDto{

  private String merchant;

  private String forwarder;

  private List<ContainerDto> containers = new ArrayList<>();
}
