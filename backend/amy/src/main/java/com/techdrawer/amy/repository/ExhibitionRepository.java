package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.City;
import com.techdrawer.amy.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    Optional<Exhibition> findByPublicId(UUID publicId);

    List<Exhibition> findByCity(City city);

}
