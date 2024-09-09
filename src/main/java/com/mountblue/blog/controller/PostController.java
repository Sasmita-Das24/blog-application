package com.mountblue.blog.controller;

import com.mountblue.blog.entity.Comment;
import com.mountblue.blog.entity.Post;
import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public String getPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "sort", defaultValue = "desc") String sortOrder, Model model){
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<Post> postPage = postService.findAllPaged(pageable);
        List<Post> posts = postService.findAll();
        List<Tag> allTags = tagService.findAllTags();
        List<String> allAuthors = postService.findAllAuthors();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Post post : posts) {
            if (post.getPublishedAt() != null) {
                post.setFormattedDate(post.getPublishedAt().format(formatter));
            } else {
                post.setFormattedDate("");
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("allTags",allTags);
        model.addAttribute("allAuthors",allAuthors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortOrder", sortOrder);

        if (!posts.isEmpty()) {
            Post firstPost = posts.get(0);
            String formattedDate = firstPost.getPublishedAt() != null ?
                    firstPost.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
            model.addAttribute("formattedDate", formattedDate);
        }
        return "posts";
    }

    @GetMapping("/posts/sort")
    public String getSortedPosts(@RequestParam(value = "page", defaultValue="0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
                           Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> postPage = postService.findAllPaged(pageable);
        List<Post> posts = postPage.getContent();
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("posts", posts);

        return "posts";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
                                  Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> posts = postService.searchPosts(query, pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("query", query); // to preserve the search query in the view
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("sortOrder", sortOrder);
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
    public String getFilteredPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) List<String> selectedAuthor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<String> selectedTags,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());

        List<Tag> allTags = tagService.findAllTags();
        List<String> allAuthors = postService.findAllAuthors();
        Page<Post> filteredPaginatedPosts = postService.filterPosts(selectedAuthor, startDate, startDate, selectedTags, pageable);
        List<Post> filteredPosts = filteredPaginatedPosts.getContent();

        model.addAttribute("allTags", allTags);
        model.addAttribute("allAuthors", allAuthors);
        model.addAttribute("posts", filteredPosts);
        model.addAttribute("selectedAuthor", selectedAuthor);
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", filteredPaginatedPosts.getTotalPages());
        model.addAttribute("sortOrder", sortOrder);

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
        String tags = String.join(",", allTags.stream().map(tag -> tag.getName()).collect(Collectors.toList()));
        model.addAttribute("tags", tags);
        return "edit-post";
    }

    @PostMapping("/posts/update")
    public String updatePost(@ModelAttribute("post") Post post,@RequestParam(value = "tagsList", required = false) String tagsString){
        List<String> tags = Arrays.asList(tagsString.split(","));
        Post existingPost =  postService.findPostById(post.getId());

        if (existingPost == null) {
            return "redirect:/posts?error=PostNotFound";
        }

       existingPost.setTitle(post.getTitle());
       existingPost.setContent(post.getContent());

        if (tags != null) {
            List<Tag> selectedTags = tagService.findTagsByName(tags);
            existingPost.setTags(selectedTags);
        } else {
            existingPost.setTags(new ArrayList<>());
        }

       postService.save(existingPost);
       return  "redirect:/posts";
    }
}
