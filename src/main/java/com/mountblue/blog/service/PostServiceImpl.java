package com.mountblue.blog.service;

import com.mountblue.blog.entity.Post;
import com.mountblue.blog.entity.Tag;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{
    private PostRepository postRepository;
    private final TagRepository tagRepository;
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
        return null;
    }

    /*
    @Override
    public List<Post> filterPosts(List<String> authors, List<Long> tagIds, LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        // Call the repository method with the filters
        return postRepository.findPostsByFilters(authors, tagIds, startDateTime, endDateTime);
    }

     */
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

    /*@Override
    public List<Post> searchPosts(String query) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrAuthor_NameContainingIgnoreCaseOrTags_NameContainingIgnoreCase(
                query, query, query, query);
    }*/

    @Override
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.search(query, pageable);
    }
}
