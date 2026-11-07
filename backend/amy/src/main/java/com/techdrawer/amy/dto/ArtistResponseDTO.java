package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ArtistSpeciality;
import com.techdrawer.amy.model.ArtisticStyle;

public record ArtistResponseDTO(

        String name,
        String bibliography,
        ArtistSpeciality speciality,
        ArtisticStyle artisticStyle,
        String profilePictureUrl,
        String portfolioUrl,
        String socialLinks,
        Boolean verificationStatus

) {
}
