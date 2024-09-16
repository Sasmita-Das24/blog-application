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
            model.addAttribute("commentName", comment);
            model.addAttribute("post", post);
            return "edit-comment";
        }
        return "error";
    }

    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable("id") Integer id, @ModelAttribute("commentName") Comment comment) {
        commentService.update(id, comment);
        return "redirect:/posts";
    }

    @PostMapping("/updateComment/{postId}/{commentId}")
    public String updateComment(@PathVariable("postId") int postId,@PathVariable("commentId") int commentId, @ModelAttribute("existingComment") Comment updatedComment, Model model){
        Post post = postService.findPostById(postId);
        Comment existingComment = commentService.getCommentById(commentId);

        // Check if the comment exists
        if (existingComment != null) {
            // Update the existing comment with the new values from the form
            existingComment.setName(updatedComment.getName());
            existingComment.setEmail(updatedComment.getEmail());
            existingComment.setComment(updatedComment.getComment());

            // Save the updated comment
            commentService.saveComment(existingComment);

            // Redirect to the post details page after saving
            return "redirect:/posts/" + postId;
        } else {
            return "error"; // Handle if comment does not exist
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
