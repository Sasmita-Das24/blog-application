package com.mountblue.blog.service;

import com.mountblue.blog.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {
    List<Post> findAll();

    Post findPostById(int id);

    void deleteById(int id);

    void save(Post post);
}
