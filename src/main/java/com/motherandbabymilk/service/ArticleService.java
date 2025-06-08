package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.ArticleRequest;
import com.motherandbabymilk.dto.response.ArticleResponse;
import com.motherandbabymilk.entity.Article;
import com.motherandbabymilk.exception.DuplicateProductException;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.ArticlesRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService{

    @Autowired
    private ArticlesRepository articlesRepository;
    @Autowired
    private ModelMapper modelMapper;


    public ArticleResponse createArticle(@Valid ArticleRequest request) {
        Article existingArticle = articlesRepository.findByTitle(request.getTitle());
        if (existingArticle!= null) {
            throw new DuplicateProductException("Article with title " + request.getTitle() + " already exists");
        }
        Article article = modelMapper.map(request, Article.class);
        article.setId(0);
        article.setDelete(false);
        Article savedArticle = articlesRepository.save(article);

        return modelMapper.map(savedArticle, ArticleResponse.class);
    }

    public ArticleResponse updateArticles(int id, @Valid ArticleRequest request) {
        Article article = articlesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Articles with ID " + id + " not found"));
        Article existingArticle = articlesRepository.findByTitle(request.getTitle());
        if (existingArticle != null && existingArticle.getId() != id) {
            throw new DuplicateProductException("Article with title " + request.getTitle() + " already exists");
        }
        modelMapper.map(request, article);
        article.setId(id);
        Article updatedArticles = articlesRepository.save(article);
        return modelMapper.map(updatedArticles, ArticleResponse.class);
    }

    public ArticleResponse getArticleById(int id) {
        Article article = articlesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article with ID " + id + " not found"));
        return modelMapper.map(article, ArticleResponse.class);
    }

    public List<ArticleResponse> getAllArticles() {
        List<Article> articles = articlesRepository.findAllNotDelete();
        return articles.stream()
                .map(Article -> modelMapper.map(Article, ArticleResponse.class))
                .collect(Collectors.toList());
    }
    public void deleteArticle(int id) {
        Article article = articlesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
        article.setDelete(true);
        articlesRepository.save(article);
    }
}


