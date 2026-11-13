package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.Media;

import java.time.LocalDateTime;
import java.util.List;

public record ExhibitionResponseDTO(

        String title,

        String description,

        List<Media> gallery,

        String organizer,

        List<ArtistResponseDTO> participatingArtists,

        LocalDateTime startDate,

        LocalDateTime endDate,

        City city,

        String location

) {
}
