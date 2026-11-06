package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.GalleryRegistrationDTO;
import com.techdrawer.amy.dto.GalleryResponseDTO;
import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.GalleryType;
import com.techdrawer.amy.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/")
    public ResponseEntity<List<GalleryResponseDTO>> getAllGallery() {

        return new ResponseEntity<>(galleryService.getAllGallery(), HttpStatus.OK);
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<GalleryResponseDTO>> getGalleryByCity(@PathVariable City city) {

        return new ResponseEntity<>(galleryService.getGalleryByCity(city), HttpStatus.OK);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<GalleryResponseDTO>> getGalleryByType(@PathVariable GalleryType type) {

        return new ResponseEntity<>(galleryService.getGalleryByType(type), HttpStatus.OK);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<GalleryResponseDTO> getGalleryById(@PathVariable UUID publicId) {

        return new ResponseEntity<>(galleryService.getGalleryById(publicId), HttpStatus.OK);
    }

    @PostMapping("/{publicId}/register")
    public ResponseEntity<GalleryResponseDTO> registerGallery(@Valid @RequestBody GalleryRegistrationDTO dto, @PathVariable UUID publicId) {

        return new ResponseEntity<>(galleryService.registerGallery(dto, publicId), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<GalleryResponseDTO> updateGallery(@Valid @RequestBody GalleryRegistrationDTO dto, @PathVariable UUID publicId) {

        return new ResponseEntity<>(galleryService.updateGallery(dto, publicId), HttpStatus.OK);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteGallery(@PathVariable UUID publicId) {

        galleryService.deleteGallery(publicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
