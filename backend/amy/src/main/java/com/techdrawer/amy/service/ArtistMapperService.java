package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArtistResponseDTO;
import com.techdrawer.amy.model.Artist;
import org.springframework.stereotype.Service;

@Service
public class ArtistMapperService {

    public ArtistResponseDTO mapToResponseDTO (Artist artist) {

        return new ArtistResponseDTO(

                artist.getUser().getName(),
                artist.getBibliography(),
                artist.getSpeciality(),
                artist.getArtisticStyle(),
                artist.getProfilePictureUrl(),
                artist.getPortfolioUrl(),
                artist.getSocialLinks(),
                artist.getVerificationStatus()

        );
    }
}
