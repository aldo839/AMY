package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArtistResponseDTO;
import com.techdrawer.amy.dto.ExhibitionResponseDTO;
import com.techdrawer.amy.model.Exhibition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExhibitionMapperService {

    private final ArtistMapperService artistMapperService;

    public ExhibitionResponseDTO mapToResponseDTO(Exhibition exhibition) {

        List<ArtistResponseDTO> artistDTOs = exhibition.getParticipatingArtists().stream()
                .map(artistMapperService::mapToResponseDTO)
                .toList();

        return new ExhibitionResponseDTO(
                exhibition.getTitle(),
                exhibition.getDescription(),
                exhibition.getGallery(),
                exhibition.getOrganizer().getName(),
                artistDTOs,
                exhibition.getStartDate(),
                exhibition.getEndDate(),
                exhibition.getCity(),
                exhibition.getLocation()
        );
    }
}
