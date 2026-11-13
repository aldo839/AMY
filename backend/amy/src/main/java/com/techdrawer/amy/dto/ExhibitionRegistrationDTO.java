package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.City;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class ExhibitionRegistrationDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private List<UUID> participatingArtistsPublicIds;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "City is required")
    private City city;

    private String location;

}
