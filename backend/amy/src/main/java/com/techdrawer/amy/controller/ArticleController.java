package com.techdrawer.amy.controller;

import com.techdrawer.amy.dto.ArticleRegistrationDTO;
import com.techdrawer.amy.dto.ArticleResponseDTO;
import com.techdrawer.amy.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/")
    public ResponseEntity<List<ArticleResponseDTO>> getAllArticles() {

        return new ResponseEntity<>(articleService.getAllArticle(), HttpStatus.OK);
    }

    @GetMapping("/artist/{publicId}")
    public ResponseEntity<List<ArticleResponseDTO>> getArticleByArtist(@PathVariable UUID publicId) {

        return new ResponseEntity<>(articleService.getArticleByArtist(publicId), HttpStatus.OK);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ArticleResponseDTO> getArticleByPublicId(@PathVariable UUID publicId) {

        return new ResponseEntity<>(articleService.getArticleByPublicId(publicId), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ArticleResponseDTO> createArticle(
            @RequestPart("data") @Valid ArticleRegistrationDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        return new ResponseEntity<>(articleService.createArticle(dto, files), HttpStatus.CREATED);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<ArticleResponseDTO> updateArticle(
            @RequestPart("data") @Valid ArticleRegistrationDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @PathVariable UUID publicId) throws AccessDeniedException {

        return new ResponseEntity<>(articleService.updateArticle(dto, files, publicId), HttpStatus.OK);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID publicId) throws AccessDeniedException {

        articleService.deleteArticle(publicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
