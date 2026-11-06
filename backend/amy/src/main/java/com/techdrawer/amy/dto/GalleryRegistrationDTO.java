package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.GalleryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class GalleryRegistrationDTO {

    @NotNull(message = "City is required")
    private City city;

    @Size(max = 200, message = "Location must not pass 200 characters")
    private String Location;

    @Size(max = 1000, message = "Description must not pass 1000 characters")
    private String description;

    @NotNull(message = "Gallery type is required")
    private GalleryType galleryType;

    private List<Artist> representedArtist;

    @Size(max = 100, message = "website must not pass 100 characters")
    private String website;

    @Size(max = 100, message = "Business registration number must not pass 100 characters")
    private String businessRegistrationNumber;

}
