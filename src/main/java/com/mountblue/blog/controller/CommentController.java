package com.mountblue.blog.controller;

import com.mountblue.blog.entity.Post;
import com.mountblue.blog.service.CommentService;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;
    private PostService postService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/save")
    public  String saveComment(@RequestParam("postId") int postId, @ModelAttribute("newComment") Comment comment){
        Post post = postService.findPostById(postId);
        comment.setPost(post);
        commentService.saveComment(comment);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/posts/{postId}/comments")
    public String viewComment(@PathVariable Integer postId, Model model){
      Post post =  postService.findPostById(postId);
      List<Comment> comments= commentService.findByPostId(postId);

      model.addAttribute("post",post);
      model.addAttribute("comments",comments);
      model.addAttribute("newComment" ,new Comment());

      return "post-details";
    }

    @GetMapping("/edit/{commentId}")
    public String showEditCommentForm(@PathVariable("commentId") int commentId, Model model) {
        Comment comment = commentService.getCommentById(commentId);
        Post post = comment.getPost();

        if (comment != null && post != null) {
            model.addAttribute("comment", comment);
            model.addAttribute("post", post);
            return "edit-comment";
        }
        return "error";
    }



    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable("id") Integer id, @ModelAttribute("comment") Comment comment) {
        commentService.update(id, comment);
        return "redirect:/posts";
    }


    @PostMapping("/updateComment/{postId}/{commentId}")
    public String updateComment(@PathVariable("postId") int postId,@PathVariable("commentId") int commentId, Model model){
        Post post = postService.findPostById(postId);
        Set<Comment> comments = post.getComments();

        Comment existingComment = commentService.getCommentById(commentId);

        if(existingComment != null){
            String theComment = existingComment.getComment();
            model.addAttribute("post",post);
            model.addAttribute("commentId", commentId);
            model.addAttribute("comments",comments);
            model.addAttribute("theComment",theComment);
            model.addAttribute("existingComment",existingComment);
            commentService.saveComment(existingComment);
            return "post-details";
        }
        else{
            return "error";
        }
    }

    @PostMapping("/delete/{comment_id}")
    public String deleteComment(@PathVariable("comment_id") int commentId){
        Comment comment =  commentService.getCommentById(commentId);
        Post post = comment.getPost();
        int postId = post.getId();
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }
}
