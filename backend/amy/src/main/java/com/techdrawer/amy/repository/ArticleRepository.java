package com.techdrawer.amy.repository;

import com.techdrawer.amy.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByPublicId(UUID publicId);

    List<Article> findByArtist_User_PublicId(UUID publicId);

}
