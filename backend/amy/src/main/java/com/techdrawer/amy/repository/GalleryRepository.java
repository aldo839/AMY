package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.Gallery;
import com.techdrawer.amy.model.GalleryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    Optional<Gallery> findByUser_PublicId(UUID publicId);

    Optional<Gallery> findByCity(City city);

    Optional<Gallery> findByGalleryType(GalleryType type);

}
