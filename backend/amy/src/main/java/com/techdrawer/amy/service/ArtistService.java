package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArtistRegistrationDTO;
import com.techdrawer.amy.dto.ArtistResponseDTO;
import com.techdrawer.amy.exception.MissingArgumentException;
import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.ArtistRepository;
import com.techdrawer.amy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapperService artistMapperService;
    private final UserRepository userRepository;
    private final AuthService authService;

    public List<ArtistResponseDTO> getAllArtists() {

        return artistRepository.findAll().stream()
                .map(artistMapperService::mapToResponseDTO)
                .toList();
    }


    public List<ArtistResponseDTO> getArtistBySpeciality(ArtistSpeciality speciality) {

        return artistRepository.findBySpeciality(speciality).stream()
                .map(artistMapperService::mapToResponseDTO)
                .toList();
    }


    public List<ArtistResponseDTO> getArtistByArtisticStyle(ArtisticStyle style) {

        return artistRepository.findByArtisticStyle(style).stream()
                .map(artistMapperService::mapToResponseDTO)
                .toList();
    }


    public ArtistResponseDTO getArtistByPublicID(UUID publicId){

        return artistRepository.findByUser_PublicId(publicId)
                .map(artistMapperService::mapToResponseDTO)
                .orElseThrow(() -> new UserNotFoundException("Artist not found"));
    }


    public ArtistResponseDTO registerArtist(ArtistRegistrationDTO dto, UUID publicId){

        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new UserNotFoundException("User not found with public_id : " + publicId));

        User currentUser = authService.getCurrentUser();

        if (!user.getId().equals(currentUser.getId())) {
            throw new BadCredentialsException("User not authenticated");
        }

        if (dto.getSpeciality() == null || dto.getArtisticStyle() == null) {
            throw new MissingArgumentException("Missing information, please register all require field");
        }

        Artist artist = new Artist();

        artist.setUser(user);
        artist.setBibliography(dto.getBibliography());
        artist.setSpeciality(dto.getSpeciality());
        artist.setArtisticStyle(dto.getArtisticStyle());
        artist.setPortfolioUrl(dto.getPortfolioUrl());
        artist.setSocialLinks(dto.getSocialLinks());

        Artist registedArtist = artistRepository.save(artist);

        return artistMapperService.mapToResponseDTO(registedArtist);
    }


    public ArtistResponseDTO updateArtist(ArtistRegistrationDTO dto, UUID publicId) {

        User currentUser = authService.getCurrentUser();

        if (!currentUser.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("User not authenticated");
        }

        Artist artist = new Artist();

        if (!dto.getBibliography().isBlank()) {
            artist.setBibliography(dto.getBibliography());
        }
        if (dto.getSpeciality() != null) {
            artist.setSpeciality(dto.getSpeciality());
        }
        if (dto.getArtisticStyle() != null) {
            artist.setArtisticStyle(dto.getArtisticStyle());
        }
        if (!dto.getPortfolioUrl().isBlank()) {
            artist.setPortfolioUrl(dto.getPortfolioUrl());
        }
        if (!dto.getSocialLinks().isBlank()) {
            artist.setSocialLinks(dto.getSocialLinks());
        }

        Artist updatedArtist = artistRepository.save(artist);

        return artistMapperService.mapToResponseDTO(updatedArtist);
    }


    public void deleteArtist(UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId) || user.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("User not authorized");
        }

        Artist artist = artistRepository.findByUser_PublicId(publicId)
                .orElseThrow(() -> new UserNotFoundException("Artist not found"));

        artistRepository.delete(artist);
    }

}
