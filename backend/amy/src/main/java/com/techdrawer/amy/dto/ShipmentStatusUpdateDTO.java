package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShipmentStatusUpdateDTO {

    @NotNull(message = "Shipment status is required")
    private ShipmentStatus status;

}