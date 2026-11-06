package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.CollectorRegistrationDTO;
import com.techdrawer.amy.dto.CollectorResponseDTO;
import com.techdrawer.amy.exception.UserNotFoundException;
import com.techdrawer.amy.model.Collector;
import com.techdrawer.amy.model.CollectorType;
import com.techdrawer.amy.model.Role;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.CollectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final CollectorRepository collectorRepository;
    private final CollectorMapperService collectorMapperService;
    private final AuthService authService;

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
                .orElseThrow(() -> new UserNotFoundException("Collector not found"));

        return collectorMapperService.mapToResponseDTO(collector);
    }


    public CollectorResponseDTO createCollector(CollectorRegistrationDTO dto, UUID publicId) {

        User user = authService.getCurrentUser();

        if (!user.getPublicId().equals(publicId)) {
            throw new BadCredentialsException("Unauthorized user");
        }

        Collector collector = new Collector();

        collector.setUser(user);
        collector.setCollectorType(dto.getCollectorType());
        collector.setInterest(dto.getInterest());
        collector.setPreferredArtists(dto.getPreferredArtists());
        collector.setPreferredStyle(dto.getPreferredStyle());
        collector.setShippingAddresses(dto.getShippingAddress());

        Collector createdCollector = collectorRepository.save(collector);

        return collectorMapperService.mapToResponseDTO(createdCollector);
    }


    public CollectorResponseDTO updateCollector(CollectorRegistrationDTO dto, UUID publicId) {

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
        if (dto.getPreferredArtists() != null) {
            collector.setPreferredArtists(dto.getPreferredArtists());
        }
        if (dto.getPreferredStyle() != null) {
            collector.setPreferredStyle(dto.getPreferredStyle());
        }
        if (dto.getShippingAddress() != null) {
            collector.setShippingAddresses(dto.getShippingAddress());
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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        collectorRepository.delete(collector);
    }

}
