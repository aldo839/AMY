package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.ArtisticStyle;
import com.techdrawer.amy.model.CollectorType;

import java.util.List;

public record CollectorResponseDTO(

        String name,

        CollectorType collectorType,

        String interest,

        List<ArtisticStyle>preferredStyle,

        List<Artist> preferredArtists,

        String shippingAddresses,

        String profilePictureUrl

) {
}
