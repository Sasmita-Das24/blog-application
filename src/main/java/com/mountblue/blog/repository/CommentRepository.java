package com.mountblue.blog.repository;

import com.mountblue.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findPostById(Integer id);
}
