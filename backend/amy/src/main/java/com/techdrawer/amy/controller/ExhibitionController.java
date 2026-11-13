package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.ExhibitionRegistrationDTO;
import com.techdrawer.amy.dto.ExhibitionResponseDTO;
import com.techdrawer.amy.model.City;
import com.techdrawer.amy.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @GetMapping("/")
    public ResponseEntity<List<ExhibitionResponseDTO>> getAllExhibition() {

        return new ResponseEntity<>(exhibitionService.getAllExhibition(), HttpStatus.OK);
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<ExhibitionResponseDTO>> getExhibitionByCity(@PathVariable City city) {

        return new ResponseEntity<>(exhibitionService.getExhibitionByCity(city), HttpStatus.OK);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ExhibitionResponseDTO> getExhibitionByPublicId(@PathVariable UUID publicId) {

        return new ResponseEntity<>(exhibitionService.getExhibitionByPublicId(publicId), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ExhibitionResponseDTO> createExhibition(
            @RequestPart(value = "data") ExhibitionRegistrationDTO dto,
            @RequestPart(value = "files", required = true) List<MultipartFile> files) {

        return new ResponseEntity<>(exhibitionService.createExhibition(dto, files), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<ExhibitionResponseDTO> updateExhibition(
            @RequestPart(value = "date") ExhibitionRegistrationDTO dto,
            @RequestPart(value = "files", required = true) List<MultipartFile> files,
            @PathVariable UUID publicId) throws AccessDeniedException {

        return new ResponseEntity<>(exhibitionService.updateExhibition(dto, files, publicId), HttpStatus.OK);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteExhibition(@PathVariable UUID publicId) throws AccessDeniedException {

        exhibitionService.deleteExhibition(publicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
