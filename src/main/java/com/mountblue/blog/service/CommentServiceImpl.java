package com.mountblue.blog.service;

import com.mountblue.blog.entity.Comment;
import com.mountblue.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class CommentServiceImpl implements  CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(Integer id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Failed to get comment"));
    }

    @Override
    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findByPostId(Integer postId) {
        return commentRepository.findPostById(postId);
    }
    @Override
    public void update(Integer id, Comment comment) {
       Comment existingComment = commentRepository.findById(id).orElseThrow(() ->
               new RuntimeException("Failed to get comment"));

        existingComment.setName(comment.getName());
        existingComment.setEmail(comment.getEmail());
        existingComment.setComment(comment.getComment());

        commentRepository.save(existingComment);
    }

    @Override
    public Comment findById(Integer id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));
    }
}