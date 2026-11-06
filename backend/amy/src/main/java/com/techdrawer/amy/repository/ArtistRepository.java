package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Artist;
import com.techdrawer.amy.model.ArtistSpeciality;
import com.techdrawer.amy.model.ArtisticStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findBySpeciality(ArtistSpeciality speciality);

    Optional<Artist> findByArtisticStyle(ArtisticStyle style);

    Optional<Artist> findByUser_PublicId(UUID publicId);

}
