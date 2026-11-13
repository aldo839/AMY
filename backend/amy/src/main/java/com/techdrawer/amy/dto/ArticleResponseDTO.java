package com.techdrawer.amy.dto;

import com.techdrawer.amy.model.ArtistSpeciality;
import com.techdrawer.amy.model.Media;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ArticleResponseDTO(

        ArtistSpeciality type,
        String title,
        String content,
        String artist,
        List<Media> images,
        BigDecimal price,
        Boolean isAvailable,
        LocalDateTime createdAt

) {
}
