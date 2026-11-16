package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArtistResponseDTO;
import com.techdrawer.amy.dto.CollectorResponseDTO;
import com.techdrawer.amy.model.Collector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectorMapperService {

    private final ArtistMapperService artistMapperService;

    public CollectorResponseDTO mapToResponseDTO(Collector collector) {

        List<ArtistResponseDTO> artistDTOs = collector.getPreferredArtists().stream()
                .map(artistMapperService::mapToResponseDTO)
                .toList();

        return new CollectorResponseDTO(

                collector.getUser().getName(),
                collector.getCollectorType(),
                collector.getInterest(),
                collector.getPreferredStyle(),
                artistDTOs,
                collector.getShippingAddresses(),
                collector.getProfilePictureUrl()
        );
    }
}
