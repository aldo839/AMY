package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.GalleryRegistrationDTO;
import com.techdrawer.amy.dto.GalleryResponseDTO;
import com.techdrawer.amy.exception.MissingArgumentException;
import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryMapperService galleryMapperService;
    private final AuthService authService;
    private final FileStorageService fileStorageService;

    public List<GalleryResponseDTO> getAllGallery() {

        return galleryRepository.findAll().stream()
                .map(galleryMapperService::mapToResponseDTO)
                .toList();
    }


    public List<GalleryResponseDTO> getGalleryByCity(City city) {

        return galleryRepository.findByCity(city).stream()
                .map(galleryMapperService::mapToResponseDTO)
                .toList();
    }


    public List<GalleryResponseDTO> getGalleryByType(GalleryType type) {

        return galleryRepository.findByGalleryType(type).stream()
                .map(galleryMapperService::mapToResponseDTO)
                .toList();
    }


    public GalleryResponseDTO getGalleryById(UUID publicId) {

        return galleryRepository.findByUser_PublicId(publicId)
                .map(galleryMapperService::mapToResponseDTO)
                .orElseThrow(() -> new UserNotFoundException("Gallery not found"));
    }


    public GalleryResponseDTO registerGallery(GalleryRegistrationDTO dto, MultipartFile file, UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("User not authorized");
        }

        if (dto.getCity() == null || dto.getGalleryType() == null) {
            throw new MissingArgumentException("Register every require field");
        }

        Gallery gallery = new Gallery();

        gallery.setUser(user);
        gallery.setCity(dto.getCity());
        gallery.setGalleryType(dto.getGalleryType());
        gallery.setDescription(dto.getDescription());
        gallery.setLocation(dto.getLocation());
        gallery.setWebsite(dto.getWebsite());
        gallery.setBusinessRegistrationNumber(dto.getBusinessRegistrationNumber());
        gallery.setRepresentedArtists(dto.getRepresentedArtist());

        if (file != null && !file.isEmpty()){
            String fileName = fileStorageService.storeFile(file);
            gallery.setProfilePictureUrl("/images/" + fileName);
        }

        Gallery registedGallery = galleryRepository.save(gallery);

        return galleryMapperService.mapToResponseDTO(registedGallery);
    }


    public GalleryResponseDTO updateGallery(GalleryRegistrationDTO dto, MultipartFile file, UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("User not authorized");
        }

        Gallery gallery = new Gallery();

        if (dto.getGalleryType() != null) {
            gallery.setGalleryType(dto.getGalleryType());
        }
        if (!dto.getDescription().isBlank()) {
            gallery.setDescription(dto.getDescription());
        }
        if (!dto.getLocation().isBlank()) {
            gallery.setLocation(dto.getLocation());
        }
        if (!dto.getBusinessRegistrationNumber().isBlank()) {
            gallery.setBusinessRegistrationNumber(dto.getBusinessRegistrationNumber());
        }
        if (dto.getCity() != null) {
            gallery.setCity(dto.getCity());
        }
        if (!dto.getWebsite().isBlank()) {
            gallery.setWebsite(dto.getWebsite());
        }
        if (file != null && !file.isEmpty()){
            String fileName = fileStorageService.storeFile(file);
            gallery.setProfilePictureUrl("/images/" + fileName);
        }

        Gallery updatedGallery = galleryRepository.save(gallery);

        return galleryMapperService.mapToResponseDTO(updatedGallery);
    }


    public void deleteGallery(UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId) || user.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("Unauthorized user");
        }

        Gallery gallery = galleryRepository.findByUser_PublicId(publicId)
                .orElseThrow(() -> new UserNotFoundException("Gallery not found"));

        galleryRepository.delete(gallery);
    }
}
