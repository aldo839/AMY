package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ArtistSpeciality;
import com.techdrawer.amy.model.ArtisticStyle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ArtistRegistrationDTO {

    @Size(min = 5, max = 1000, message = "Bibliography must contain between 5 and 1000 characters")
    private String bibliography;

    @NotNull(message = "Speciality is required")
    private ArtistSpeciality speciality;

    @NotNull(message = "Artistic style is required")
    private ArtisticStyle artisticStyle;

    @Size(max = 500, message = "Portfolio URL should not pass 500 characters")
    private String portfolioUrl;

    @Size(max = 500, message = "Social media links should not pass 500 characters")
    private String socialLinks;

}
