package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ArtistSpeciality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ArticleRegistrationDTO {

    @NotNull(message = "Type is required")
    private ArtistSpeciality type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Price is required")
    @Positive(message = "Price can't be negative")
    private BigDecimal price;

}
