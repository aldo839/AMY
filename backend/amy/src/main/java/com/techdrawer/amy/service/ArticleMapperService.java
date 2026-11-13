package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArticleResponseDTO;
import com.techdrawer.amy.model.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleMapperService {

    public ArticleResponseDTO mapToResponseDTO(Article article) {

        return new ArticleResponseDTO(

                article.getType(),
                article.getTitle(),
                article.getContent(),
                article.getArtist().getUser().getName(),
                article.getImages(),
                article.getPrice(),
                article.getIsAvailable(),
                article.getCreatedAt()

        );
    }
}
