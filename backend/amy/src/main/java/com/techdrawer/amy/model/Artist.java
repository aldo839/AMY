package com.techdrawer.amy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bibliography;

    @Enumerated(EnumType.STRING)
    private ArtistSpeciality speciality;

    @Enumerated(EnumType.STRING)
    private ArtisticStyle artisticStyle;

    private String portfolioUrl;

    private String socialLinks;

    private Boolean verificationStatus = false;

}
