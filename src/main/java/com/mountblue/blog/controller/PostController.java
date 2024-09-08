package com.mountblue.blog.controller;

import com.mountblue.blog.entity.Comment;
import com.mountblue.blog.entity.Post;
import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class PostController {
    private final PostService postService;
    private final TagService tagService;

    private final PostRepository postRepository;

    @Autowired
    public PostController(PostService postService, TagService tagService, PostRepository postRepository) {
        this.postService = postService;
        this.tagService = tagService;
        this.postRepository = postRepository;
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
    public  String savePost(@ModelAttribute("post") Post post, @RequestParam("tagList") String tagList){
        if (post.getTags() == null) {
            post.setTags(new ArrayList<>());
        }

        List<Tag> tags = new ArrayList<>();
        String[] tagNames = tagList.split("\\s*,\\s*");
        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tags.add(tag);
        }
        post.setTags(tags);
        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String getPosts(Model model){
        List<Post> posts = postService.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Iterate through each post and format the publishedAt date
        for (Post post : posts) {
            if (post.getPublishedAt() != null) {
                post.setFormattedDate(post.getPublishedAt().format(formatter));
            } else {
                post.setFormattedDate("");
            }
        }

        // Add the list of posts with formatted dates to the model
        model.addAttribute("posts", posts);

        // Optionally, format the date for the first post and add it separately
        if (!posts.isEmpty()) {
            Post firstPost = posts.get(0);
            String formattedDate = firstPost.getPublishedAt() != null ?
                    firstPost.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
            model.addAttribute("formattedDate", formattedDate);
        }
        return "posts";
    }

    @GetMapping("/posts/sort")
    public String getPosts(@RequestParam(defaultValue = "desc") String sortOrder, Model model) {
        List<Post> posts = postService.findAllSortedByPublishedAt(sortOrder);
        model.addAttribute("posts", posts);
        model.addAttribute("sortOrder", sortOrder);
        return "posts";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query, @PageableDefault(size = 10) Pageable pageable,
                                  Model model) {
        Page<Post> posts = postService.searchPosts(query, pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("query", query); // to preserve the search query in the view
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

    @GetMapping("/posts/filter")
    public String getFilteredPosts(@RequestParam(required = false) List<String> selectedAuthors,
                                   @RequestParam(required = false) List<Long> selectedTags,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, Model model){
        //List<String> tagList = (tags != null) ? Arrays.asList(tags.split(",")) : Collections.emptyList();
        List<Tag> allTags = tagService.findAllTags();
        List<String> allAuthors = postService.findAllAuthors();


        //List<Post> filteredPosts = postService.filterPosts(selectedAuthors,selectedTags, startDate,endDate);

        model.addAttribute("allTags", allTags);
        model.addAttribute("allAuthors", allAuthors);
       // model.addAttribute("posts", filteredPosts);
        model.addAttribute("selectedAuthors", selectedAuthors);
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "posts";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable int id){
        postService.deleteById(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String editPost(@PathVariable int id, Model model) {
        Post post = postService.findPostById(id);
        List<Tag> allTags = post.getTags();
        model.addAttribute("post", post);
        model.addAttribute("allTags", allTags);
        return "edit-post";
    }

    @PostMapping("/posts/update")
    public String updatePost(@ModelAttribute("post") Post post,@RequestParam(value = "tags", required = false) List<Integer> tagIds){
       Post existingPost =  postService.findPostById(post.getId());

        if (existingPost == null) {
            // Handle case where post is not found, e.g., redirect with an error message
            return "redirect:/posts?error=PostNotFound";
        }

       existingPost.setTitle(post.getTitle());
       existingPost.setContent(post.getContent());

        if (tagIds != null) {
            // Fetch the selected tags from the database based on the tagIds from the form
            List<Tag> selectedTags = tagService.findTagsByIds(tagIds);
            existingPost.setTags(selectedTags);
        } else {
            // If no tags are selected, clear the tags
            existingPost.setTags(new ArrayList<>());
        }

       postService.save(existingPost);

       return  "redirect:/posts";
    }
}
