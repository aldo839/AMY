package com.techdrawer.amy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String City;

    private String Location;

    private String description;

    private GalleryType galleryType;

    private String website;

    private String businessRegistrationNumber;

    @ManyToMany
    @JoinTable(
        name = "gallery_artists",
        joinColumns = @JoinColumn(name = "gallery_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> representedArtists = new ArrayList<>();

    private Boolean verificationStatus;

}
