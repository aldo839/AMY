package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.ArtisticStyle;
import com.techdrawer.amy.model.CollectorType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class CollectorRegistrationDTO {

    @NotNull(message = "Collector type is require")
    private CollectorType collectorType;

    private String interest;

    private List<ArtisticStyle> preferredStyle;

    private List<Artist> preferredArtists;

    @Size(max = 500, message = "Shipping address must not contain more than 500 characters")
    private String shippingAddress;

}
