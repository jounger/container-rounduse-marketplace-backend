package com.crm.models;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "inbound")
@PrimaryKeyJoinColumn(name = "supply_id")
public class Inbound extends Supply {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "forwarder_id")
  private Forwarder forwarder;

  @Column(name = "empty_time")
  private LocalDateTime emptyTime;

  @Column(name = "pickup_time")
  private LocalDateTime pickupTime;

  @Column(name = "return_station")
  private String returnStation;

  @OneToOne(mappedBy = "inbound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private BillOfLading billOfLading;

}
