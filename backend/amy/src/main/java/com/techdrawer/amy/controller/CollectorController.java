package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.CollectorRegistrationDTO;
import com.techdrawer.amy.dto.CollectorResponseDTO;
import com.techdrawer.amy.model.CollectorType;
import com.techdrawer.amy.service.CollectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/collectors")
@RequiredArgsConstructor
public class CollectorController {

    private final CollectorService collectorService;

    @GetMapping("/")
    public ResponseEntity<List<CollectorResponseDTO>> getAllCollector() {

        return new ResponseEntity<>(collectorService.getAllCollector(), HttpStatus.OK);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CollectorResponseDTO>> getCollectorByType(@PathVariable CollectorType type) {

        return new ResponseEntity<>(collectorService.getCollectorByType(type), HttpStatus.OK);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<CollectorResponseDTO> getCollectorByPublicId(@PathVariable UUID publicId) {

        return new ResponseEntity<>(collectorService.getCollectorByPublicId(publicId), HttpStatus.OK);
    }

    @PostMapping("/{publicId}/register")
    public ResponseEntity<CollectorResponseDTO> createCollector(
            @RequestPart("data") @Valid CollectorRegistrationDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable UUID publicId
    ) {

        return new ResponseEntity<>(collectorService.createCollector(dto, file, publicId), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<CollectorResponseDTO> updateCollector(
            @RequestPart("data") @Valid CollectorRegistrationDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable UUID publicId
    ) {

        return new ResponseEntity<>(collectorService.updateCollector(dto, file, publicId), HttpStatus.OK);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteCollector(@PathVariable UUID publicId) {

        collectorService.deleteCollector(publicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
