package com.mountblue.blog.service;

import com.mountblue.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface PostService {
    List<Post> findAll();

    Post findPostById(int id);

    void deleteById(int id);

    void save(Post post);

    List<Post> filterPosts(String author, LocalDateTime publishedAt, List<String> tags);

    List<String> findAllAuthors();

    List<Post> findAllSortedByPublishedAt(String sortOrder);

    Page<Post> searchPosts(String query, Pageable pageable);


    List<Post> getPostsByPage(int offset, int limit);

}
