package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "shipping_notification")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "notification_id")
public class ShippingNotification extends Notification {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipping_info")
  private ShippingInfo relatedResource;

  // EnumDriverNotification
  @Column(name = "action")
  @NotBlank
  @Size(min = 2, max = 20)
  private String action;
}
