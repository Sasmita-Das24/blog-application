package com.mountblue.blog.service;

import com.mountblue.blog.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    void saveComment(Comment comment);

    Comment getCommentById(Integer id);

    void deleteComment(int id);

    List<Comment> findByPostId(Integer postId);

    void update(Integer id, Comment comment);

    Comment findById(Integer id);
}
