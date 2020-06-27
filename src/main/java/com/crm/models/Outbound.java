package com.crm.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
@Table(name = "outbound")
@PrimaryKeyJoinColumn(name = "supply_id")
public class Outbound extends Supply {

  @ManyToOne
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;
  
  @OneToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;
  
  @Column(name = "goods_description")
  private String goodsDescription;

  @Column(name = "packing_time")
  private LocalDateTime packingTime;  

  @Column(name = "packing_station")
  private String packingStation;

  @Column(name = "payload")
  private Double payload;

  //EnumUnit
  @Column(name = "unit_of_measurment")
  private String unitOfMeasurement;
  
  //EnumSupplyStatus
  private String status;
}
