package com.mountblue.blog.repository;

import com.mountblue.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    Post findPostById(int id);
}
