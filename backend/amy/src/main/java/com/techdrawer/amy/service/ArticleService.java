package com.techdrawer.amy.service;

import com.techdrawer.amy.dto.ArticleRegistrationDTO;
import com.techdrawer.amy.dto.ArticleResponseDTO;
import com.techdrawer.amy.exception.ResourceNotFoundException;
import com.techdrawer.amy.model.*;
import com.techdrawer.amy.repository.ArticleRepository;
import com.techdrawer.amy.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapperService articleMapperService;
    private final ArtistRepository artistRepository;
    private final AuthService authService;
    private final FileStorageService fileStorageService;


    public List<ArticleResponseDTO> getAllArticle() {

        return articleRepository.findAll().stream()
                .map(articleMapperService::mapToResponseDTO)
                .toList();
    }


    public List<ArticleResponseDTO> getArticleByArtist(UUID publicId) {

        Artist artist = artistRepository.findByUser_PublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        return articleRepository.findByArtist_User_PublicId(artist.getUser().getPublicId()).stream()
                .map(articleMapperService::mapToResponseDTO)
                .toList();
    }


    public ArticleResponseDTO getArticleByPublicId(UUID publicId) {

        Article article = articleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return articleMapperService.mapToResponseDTO(article);
    }


    public ArticleResponseDTO createArticle(ArticleRegistrationDTO dto, List<MultipartFile> files) {

        User user = authService.getCurrentUser();

        Artist artist = artistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist profile not found for this actions"));

        Article  article = new Article();

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setType(dto.getType());
        article.setPrice(dto.getPrice());
        article.setArtist(artist);

        if (files != null && !files.isEmpty()) {
            List<Media> mediaList = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);

                    Media media = new Media();

                    media.setFileName(fileName);
                    media.setFileUrl("/images/" + fileName);
                    media.setFileType(file.getContentType());

                    // The first image will be defined as cover
                    media.setIsCover(i == 0);

                    mediaList.add(media);
                }
            }
            article.setImages(mediaList);
        }

        Article savedArticle = articleRepository.save(article);

        return articleMapperService.mapToResponseDTO(savedArticle);
    }


    public ArticleResponseDTO updateArticle(ArticleRegistrationDTO dto, List<MultipartFile> files, UUID publicId) throws AccessDeniedException {

        User user = authService.getCurrentUser();

        Article article = articleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (!user.getId().equals(article.getArtist().getId())) {
            throw new AccessDeniedException("You can't update this article");
        }

        if (!dto.getTitle().isBlank()) {
            article.setTitle(dto.getTitle());
        }
        if (!dto.getContent().isBlank()) {
            article.setContent(dto.getContent());
        }
        if (dto.getType() != null) {
            article.setType(dto.getType());
        }
        if (dto.getPrice() != null) {
            article.setPrice(dto.getPrice());
        }

        if (files != null && !files.isEmpty()) {
            List<Media> mediaList = new ArrayList<>();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                if (file != null && !file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file);

                    Media media = new Media();

                    media.setFileName(fileName);
                    media.setFileUrl("/images/" + fileName);
                    media.setFileType(file.getContentType());

                    // The first image will be defined as cover
                    media.setIsCover(i == 0);

                    mediaList.add(media);
                }
            }
            article.setImages(mediaList);
        }

        Article updatedArticle = articleRepository.save(article);

        return articleMapperService.mapToResponseDTO(updatedArticle);
    }


    public void deleteArticle(UUID publicId) throws AccessDeniedException {

        User user = authService.getCurrentUser();


        Article article = articleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (!user.getId().equals(article.getArtist().getId()) || !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You can't delete this article");
        }

        articleRepository.delete(article);
    }

}
