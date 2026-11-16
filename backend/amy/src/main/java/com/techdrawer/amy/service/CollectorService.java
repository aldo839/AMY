package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CollectorRegistrationDTO;
import com.techdrawer.amy.dto.CollectorResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.ArtistRepository;
import com.techdrawer.amy.repository.CollectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorMapperService collectorMapperService;
    private final AuthService authService;
    private final ArtistRepository artistRepository;
    private final FileStorageService fileStorageService;

    public List<CollectorResponseDTO> getAllCollector() {

        return collectorRepository.findAll().stream()
                .map(collectorMapperService::mapToResponseDTO)
                .toList();
    }


    public List<CollectorResponseDTO> getCollectorByType(CollectorType type) {

        return collectorRepository.findByCollectorType(type).stream()
                .map(collectorMapperService::mapToResponseDTO)
                .toList();
    }


    public CollectorResponseDTO getCollectorByPublicId(UUID publicId) {

        Collector collector = collectorRepository.findByUser_PublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector not found"));

        return collectorMapperService.mapToResponseDTO(collector);
    }


    public CollectorResponseDTO createCollector(CollectorRegistrationDTO dto, MultipartFile file, UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("Unauthorized user");
        }

        Collector collector = new Collector();

        collector.setUser(user);
        collector.setCollectorType(dto.getCollectorType());
        collector.setInterest(dto.getInterest());
        collector.setPreferredStyle(dto.getPreferredStyle());
        collector.setShippingAddresses(dto.getShippingAddress());

        if (dto.getPreferredArtistsPublicIds() != null && !dto.getPreferredArtistsPublicIds().isEmpty()) {
            List<Artist> preferredArtists = artistRepository.findByUser_PublicIdIn(dto.getPreferredArtistsPublicIds());
            collector.setPreferredArtists(preferredArtists);
        }

        if (file != null && !file.isEmpty()){
            String fileName = fileStorageService.storeFile(file);
            collector.setProfilePictureUrl("/images/" + fileName);
        }

        Collector createdCollector = collectorRepository.save(collector);

        return collectorMapperService.mapToResponseDTO(createdCollector);
    }


    public CollectorResponseDTO updateCollector(CollectorRegistrationDTO dto, MultipartFile file, UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("Unauthorized user");
        }

        Collector collector = new Collector();

        if (dto.getCollectorType() != null) {
            collector.setCollectorType(dto.getCollectorType());
        }
        if (dto.getInterest() != null) {
            collector.setInterest(dto.getInterest());
        }
        if (dto.getPreferredArtistsPublicIds() != null && !dto.getPreferredArtistsPublicIds().isEmpty()) {
            List<Artist> preferredArtists = artistRepository.findByUser_PublicIdIn(dto.getPreferredArtistsPublicIds());
            collector.setPreferredArtists(preferredArtists);
        }
        if (dto.getPreferredStyle() != null) {
            collector.setPreferredStyle(dto.getPreferredStyle());
        }
        if (dto.getShippingAddress() != null) {
            collector.setShippingAddresses(dto.getShippingAddress());
        }
        if (file != null && !file.isEmpty()){
            String fileName = fileStorageService.storeFile(file);
            collector.setProfilePictureUrl("/images/" + fileName);
        }

        Collector updatedCollector = collectorRepository.save(collector);

        return collectorMapperService.mapToResponseDTO(updatedCollector);
    }


    public void deleteCollector(UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId) || user.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("Unauthorized user");
        }

        Collector collector = collectorRepository.findByUser_PublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        collectorRepository.delete(collector);
    }

}
