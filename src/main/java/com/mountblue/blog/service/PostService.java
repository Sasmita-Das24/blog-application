package com.mountblue.blog.service;

import com.mountblue.blog.entity.Post;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface PostService {
    List<Post> findAll();

    Post findPostById(int id);

    void deleteById(int id);

    void save(Post post);

    List<Post> filterPosts(String author, LocalDateTime publishedAt, List<String> tags);

   // List<Post> filterPosts(List<String> authors, List<Long> tagIds, LocalDate startDate, LocalDate endDate);

    List<String> findAllAuthors();

    List<Post> findAllSortedByPublishedAt(String sortOrder);
}
