package com.techdrawer.amy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "collectors")
public class Collector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private CollectorType collectorType;

    private String interest;

    private String preferredStyle;

    @ManyToMany
    @JoinTable(
            name = "collector_artist",
            joinColumns = @JoinColumn(name = "collector_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> preferredArtists = new ArrayList<>();

    private String shippingAddresses;

}
