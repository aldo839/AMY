package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.GalleryResponseDTO;
import com.techdrawer.amy.model.Gallery;
import org.springframework.stereotype.Service;

@Service
public class GalleryMapperService {

    public GalleryResponseDTO mapToResponseDTO(Gallery gallery) {

        return new GalleryResponseDTO(

                gallery.getUser().getName(),
                gallery.getCity(),
                gallery.getDescription(),
                gallery.getGalleryType(),
                gallery.getProfilePictureUrl(),
                gallery.getWebsite(),
                gallery.getBusinessRegistrationNumber(),
                gallery.getRepresentedArtists()

        );
    }
}
