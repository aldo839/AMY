package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.ArtistRegistrationDTO;
import com.techdrawer.amy.dto.ArtistResponseDTO;
import com.techdrawer.amy.model.ArtistSpeciality;
import com.techdrawer.amy.model.ArtisticStyle;
import com.techdrawer.amy.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistResponseDTO>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ArtistResponseDTO> getArtistByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(artistService.getArtistByPublicID(publicId));
    }

    @GetMapping("/speciality/{speciality}")
    public ResponseEntity<List<ArtistResponseDTO>> getArtistBySpeciality(@PathVariable ArtistSpeciality speciality) {
        return ResponseEntity.ok(artistService.getArtistBySpeciality(speciality));
    }

    @GetMapping("/style/{style}")
    public ResponseEntity<List<ArtistResponseDTO>> getArtistByArtisticStyle(@PathVariable ArtisticStyle style) {
        return ResponseEntity.ok(artistService.getArtistByArtisticStyle(style));
    }

    @PostMapping("/{publicId}/register")
    public ResponseEntity<ArtistResponseDTO> register(@Valid @RequestBody ArtistRegistrationDTO dto, @PathVariable UUID publicId) {
        return new ResponseEntity<>(artistService.registerArtist(dto, publicId), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<ArtistResponseDTO> update(@Valid @RequestBody ArtistRegistrationDTO dto, @PathVariable UUID publicId) {
        return ResponseEntity.ok(artistService.updateArtist(dto, publicId));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        artistService.deleteArtist(publicId);
        return ResponseEntity.noContent().build();
    }
}