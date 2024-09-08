package com.mountblue.blog.service;

import com.mountblue.blog.entity.Post;
import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{
    private PostRepository postRepository;
    private final TagRepository tagRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public PostServiceImpl(PostRepository postRepository,TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }
    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findPostById(int id) {
        return postRepository.findPostById(id);
    }

    @Override
    public void deleteById(int id) {
        postRepository.deleteById(id);
    }

    @Override
    public void save(Post post) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : post.getTags()) {
            Tag existingTag = tagRepository.findByName(tag.getName());
            if (existingTag != null) {
                tags.add(existingTag);
            } else {
                tags.add(tagRepository.save(tag));  // Save the new tag
            }
        }
        post.setTags(tags);
        postRepository.save(post);
    }

    @Override
    public List<Post> filterPosts(String author, LocalDateTime publishedAt, List<String> tags) {
        if (author != null && publishedAt != null && tags != null) {
            // Filter by author, published date, and tags
            return postRepository.findPostsByAuthorAndPublishedAtAndTags(author, publishedAt, tags);
        } else if (author != null && publishedAt != null) {
            // Filter by author and published date
            return postRepository.findPostsByAuthorAndPublishedAt(author, publishedAt);
        } else if (author != null && tags != null) {
            // Filter by author and tags
            return postRepository.findPostsByAuthorAndTags(author, tags);
        } else if (publishedAt != null && tags != null) {
            // Filter by published date and tags
            return postRepository.findPostsByPublishedAtAndTags(publishedAt, tags);
        } else if (author != null) {
            // Filter by author only
            return postRepository.findPostsByAuthor(author);
        } else if (publishedAt != null) {
            // Filter by published date only
            return postRepository.findPostsByPublishedAt(publishedAt);
        } else if (tags != null) {
            // Filter by tags only
            return postRepository.findPostsByTags(tags);
        } else {
            // No filters provided, return all posts
            return postRepository.findAll();
        }
    }

    @Override
    public List<String> findAllAuthors() {
        return postRepository.findDistinctAuthors();
    }

    @Override
    public List<Post> findAllSortedByPublishedAt(String sortOrder) {
        List<Post> posts = postRepository.findAll();
        Comparator<Post> comparator = Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder()));

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        posts.sort(comparator);
        return posts;
    }

    @Override
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.search(query, pageable);
    }

    @Override
    public List<Post> getPostsByPage(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.getContent();
    }





}
