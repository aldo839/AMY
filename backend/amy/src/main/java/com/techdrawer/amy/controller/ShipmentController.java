package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.ShipmentStatusUpdateDTO;
import com.techdrawer.amy.dto.ShipmentResponseDTO;
import com.techdrawer.amy.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDTO> getShipment(@PathVariable Long id) {

        return new ResponseEntity<>(shipmentService.getShipment(id), HttpStatus.OK);
    }

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<List<ShipmentResponseDTO>> getOrderShipments(
            @PathVariable java.util.UUID orderNumber) {

        return new ResponseEntity<>(shipmentService.getOrderShipments(orderNumber), HttpStatus.OK);
    }

    @GetMapping("/my-shipments")
    public ResponseEntity<List<ShipmentResponseDTO>> getMyShipments() {

        return new ResponseEntity<>(shipmentService.getMyShipments(), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponseDTO> updateShipmentStatus(
            @PathVariable Long id, @Valid @RequestBody ShipmentStatusUpdateDTO dto) {

        return new ResponseEntity<>(shipmentService.updateShipmentStatus(id, dto.getStatus()), HttpStatus.OK);
    }

}
