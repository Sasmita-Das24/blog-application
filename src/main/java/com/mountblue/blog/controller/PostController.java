package com.mountblue.blog.controller;

import com.mountblue.blog.entity.Comment;
import com.mountblue.blog.entity.Post;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/home")
    public String home(Model model){
        model.addAttribute("message","Hello thymeleaf");
        return "home";
    }

    @GetMapping("/newpost")
    public String createPost(Model model){
        model.addAttribute("post",new Post());
        return "create-post";
    }

    @PostMapping("/posts")
    public  String savePost(@ModelAttribute("post") Post post){
        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String getPosts(Model model){
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "posts";
    }

    @GetMapping("/posts/{id}")
    public String getPostById(@PathVariable int id, Model model){
      Post post = postService.findPostById(id);
      model.addAttribute("post",post);
      Comment newComment = new Comment();
      model.addAttribute("newComment",newComment);
      return "post-details";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable int id){
        postService.deleteById(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String editPost(@PathVariable int id, Model model) {
        Post post = postService.findPostById(id);
        model.addAttribute("post", post);
        return "edit-post";
    }

    @PostMapping("/posts/update")
    public String updatePost(@ModelAttribute("post") Post post){
       Post existingPost =  postService.findPostById(post.getId());

        if (existingPost == null) {
            // Handle case where post is not found, e.g., redirect with an error message
            return "redirect:/posts?error=PostNotFound";
        }

       existingPost.setTitle(post.getTitle());
       existingPost.setContent(post.getContent());

       postService.save(existingPost);

       return  "redirect:/posts";
    }
}
