package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.GalleryType;

import java.util.List;

public record GalleryResponseDTO(

        String name,
        City city,
        String Location,
        GalleryType galleryType,
        String profilePictureUrl,
        String website,
        String businessRegistrationNumber,
        List<Artist>representedArtists

) {

}
