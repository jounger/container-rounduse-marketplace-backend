package com.crm.payload.request;

<<<<<<< HEAD
=======
import javax.validation.constraints.NotBlank;

>>>>>>> master
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundRequest extends SupplyRequest {

  private BillOfLadingRequest billOfLading;

<<<<<<< HEAD
=======
  @NotBlank
>>>>>>> master
  private String pickupTime;

  private String emptyTime;
}
