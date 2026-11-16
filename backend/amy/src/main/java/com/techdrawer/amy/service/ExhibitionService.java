package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ExhibitionRegistrationDTO;
import com.techdrawer.amy.dto.ExhibitionResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.ArtistRepository;
import com.techdrawer.amy.repository.ExhibitionRepository;
import com.techdrawer.amy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapperService exhibitionMapperService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final FileStorageService fileStorageService;

    public List<ExhibitionResponseDTO> getAllExhibition() {

        return exhibitionRepository.findAll().stream()
                .map(exhibitionMapperService::mapToResponseDTO)
                .toList();
    }

    public List<ExhibitionResponseDTO> getExhibitionByCity(City city) {

        return exhibitionRepository.findByCity(city).stream()
                .map(exhibitionMapperService::mapToResponseDTO)
                .toList();
    }

    public ExhibitionResponseDTO getExhibitionByPublicId(UUID publicId) {

        Exhibition exhibition = exhibitionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibition not found"));

        return exhibitionMapperService.mapToResponseDTO(exhibition);
    }

    public ExhibitionResponseDTO createExhibition(ExhibitionRegistrationDTO dto, List<MultipartFile> files) {

        User user = authService.getCurrentUser();

        User organizer = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Exhibition exhibition = new Exhibition();

        exhibition.setTitle(dto.getTitle());
        exhibition.setDescription(dto.getDescription());
        exhibition.setOrganizer(organizer);
        exhibition.setStartDate(dto.getStartDate());
        exhibition.setEndDate(dto.getEndDate());
        exhibition.setCity(dto.getCity());
        exhibition.setLocation(dto.getLocation());

        if (dto.getParticipatingArtistsPublicIds() != null && !dto.getParticipatingArtistsPublicIds().isEmpty()) {
            List<Artist> participatingArtists = artistRepository.findByUser_PublicIdIn(dto.getParticipatingArtistsPublicIds());
            exhibition.setParticipatingArtists(participatingArtists);
        }

        if (files != null && !files.isEmpty()) {
            List<Media> mediaList = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);

                    Media media = new Media();

                    media.setFileName(fileName);
                    media.setFileUrl("/images/" + fileName);
                    media.setFileType(file.getContentType());

                    // The first image will be defined as cover
                    media.setIsCover(i == 0);

                    mediaList.add(media);
                }
            }
            exhibition.setGallery(mediaList);
        }

        Exhibition savedExhibition = exhibitionRepository.save(exhibition);

        return exhibitionMapperService.mapToResponseDTO(savedExhibition);
    }

    public ExhibitionResponseDTO updateExhibition(ExhibitionRegistrationDTO dto, List<MultipartFile> files, UUID publicId) throws AccessDeniedException {

        User user = authService.getCurrentUser();

        Exhibition exhibition = exhibitionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibition not found"));

        if (!user.getId().equals(exhibition.getOrganizer().getId())) {
            throw new AccessDeniedException("You can't update this exhibition");
        }

        if (!dto.getTitle().isBlank()) {
            exhibition.setTitle(dto.getTitle());
        }
        if (!dto.getDescription().isBlank()) {
            exhibition.setDescription(dto.getDescription());
        }
        if (dto.getStartDate() != null) {
            exhibition.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            exhibition.setEndDate(dto.getEndDate());
        }
        if (dto.getCity() != null) {
            exhibition.setCity(dto.getCity());
        }
        if (!dto.getLocation().isBlank()) {
            exhibition.setLocation(dto.getLocation());
        }

        if (files != null && !files.isEmpty()) {
            List<Media> mediaList = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);

                    Media media = new Media();

                    media.setFileName(fileName);
                    media.setFileUrl("/images/" + fileName);
                    media.setFileType(file.getContentType());

                    // The first image will be defined as cover
                    media.setIsCover(i == 0);

                    mediaList.add(media);
                }
            }
            exhibition.setGallery(mediaList);
        }

        Exhibition updatedExhibition = exhibitionRepository.save(exhibition);

        return exhibitionMapperService.mapToResponseDTO(updatedExhibition);
    }

    public void deleteExhibition(UUID publicId) throws AccessDeniedException {

        User user = authService.getCurrentUser();

        Exhibition exhibition = exhibitionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibition not found"));

        if (!user.getId().equals(exhibition.getOrganizer().getId())) {
            throw new AccessDeniedException("You can't delete this exhibition");
        }

        exhibitionRepository.delete(exhibition);
    }

}
