package com.crm.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;

  @Column(name = "goods_description")
  @Lob
  private String goodsDescription;

  @Column(name = "packing_time")
  private LocalDateTime packingTime;

  @Column(name = "packing_station")
  private String packingStation;

  @Column(name = "gross_weight")
  private Double grossWeight;

  @Column(name = "delivery_time")
  private LocalDateTime deliveryTime;

  // EnumUnit
  @Column(name = "unit_of_measurment")
  private String unitOfMeasurement;

  // EnumSupplyStatus
  @Column(name = "status")
  private String status;

  @OneToOne(mappedBy = "outbound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Booking booking;

  @OneToMany(mappedBy = "outbound")
  private Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
}
