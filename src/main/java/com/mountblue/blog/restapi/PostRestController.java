package com.mountblue.blog.restapi;

import com.mountblue.blog.entity.Post;
import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.repository.TagRepository;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/postss")
public class PostRestController {
    private PostRepository postRepository;
    private PostService postService;
    private TagRepository tagRepository;
    private TagService tagService;

    @Autowired
    public PostRestController(PostRepository postRepository, PostService postService, TagRepository tagRepository,TagService tagService) {
        this.postRepository = postRepository;
        this.postService = postService;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post){
        List<Tag> tags = post.getTagsList().stream().map(tagString -> {
                    Tag tag = tagRepository.findByName(tagString);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagString);
                    }
                    tagRepository.save(tag);
                    return tag;
        })
                .collect(Collectors.toList());
        post.setTags(tags);
        Post savedPost =   postRepository.save(post);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @GetMapping()
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage= postRepository.findAll(pageable);
        if(postsPage.isEmpty()){
            return  ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.ok(postsPage);
        }
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Post> getPostById(@PathVariable int id){
        Post post = postRepository.findPostById(id);
        if(post == null )
            return  ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(post);
    }

    @PreAuthorize("(hasRole('AUTHOR') and #post.author != null and #post.author.toLowerCase() == authentication.principal.username.toLowerCase()) or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Post> upddatePost(@PathVariable int id,@RequestBody Post postDetails){
        Post post = postRepository.findPostById(id);

        if(post == null){
            return ResponseEntity.notFound().build();
        }
        post.setTitle(post.getTitle());
        post.setContent(post.getContent());
        post.setExcerpt(post.getExcerpt());

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }


    @PreAuthorize("(hasRole('AUTHOR') and #post.author != null and #post.author.toLowerCase() == authentication.principal.username.toLowerCase()) or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        if (optionalPost.isPresent()) {
            postRepository.delete(optionalPost.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPosts(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "publishedAt"));

        Page<Post> postsPage = postService.searchPosts(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("currentPage", postsPage.getNumber());
        response.put("totalItems", postsPage.getTotalElements());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("sortOrder", sortOrder);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getFilteredPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) List<String> selectedAuthor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<String> selectedTags) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "publishedAt"));

        Page<Post> filteredPaginatedPosts = postService.filterPosts(selectedAuthor, startDate, endDate, selectedTags, pageable);
        List<Post> filteredPosts = filteredPaginatedPosts.getContent();

        List<Tag> allTags = tagService.findAllTags();
        List<String> allAuthors = postService.findAllAuthors();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", filteredPosts);
        response.put("currentPage", filteredPaginatedPosts.getNumber());
        response.put("totalItems", filteredPaginatedPosts.getTotalElements());
        response.put("totalPages", filteredPaginatedPosts.getTotalPages());
        response.put("sortOrder", sortOrder);
        response.put("allTags", allTags);
        response.put("allAuthors", allAuthors);
        response.put("selectedAuthor", selectedAuthor);
        response.put("selectedTags", selectedTags);
        response.put("startDate", startDate);
        response.put("endDate", endDate);

        return ResponseEntity.ok(response);
    }
}
