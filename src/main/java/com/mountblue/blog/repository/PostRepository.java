package com.mountblue.blog.repository;

import com.mountblue.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer>  {
    Post findPostById(int id);

    List<Post> findByIsPublishedTrue(Sort sort);

    @Query("SELECT DISTINCT p.author FROM Post p")
    List<String> findDistinctAuthors();

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE (:authors IS NULL OR p.author IN :authors) " +
            "AND (:tagIds IS NULL OR t.id IN :tagIds) " +
            "AND (:startDate IS NULL OR p.publishedAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.publishedAt <= :endDate)")
    List<Post> findPostsByFilters(
            @Param("authors") List<String> authors,
            @Param("tagIds") List<Long> tagIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.author) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Post> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.author = :author AND p.publishedAt = :publishedAt AND t.name IN :tags")
    List<Post> findPostsByAuthorAndPublishedAtAndTags(@Param("author") String author, @Param("publishedAt") LocalDateTime publishedAt, @Param("tags") List<String> tags);

    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.publishedAt = :publishedAt")
    List<Post> findPostsByAuthorAndPublishedAt(@Param("author") String author, @Param("publishedAt") LocalDateTime publishedA);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.author = :author AND t.name IN :tags")
    List<Post> findPostsByAuthorAndTags(@Param("author") String author, @Param("tags") List<String> tags);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.publishedAt = :publishedAt AND t.name IN :tags")
    List<Post> findPostsByPublishedAtAndTags(@Param("publishedAt") LocalDateTime publishedAt, @Param("tags") List<String> tags);

    @Query("SELECT p FROM Post p WHERE p.author = :author")
    List<Post> findPostsByAuthor(@Param("author") String author);

    @Query("SELECT p FROM Post p WHERE p.publishedAt = :publishedAt")
    List<Post> findPostsByPublishedAt(@Param("publishedAt") LocalDateTime publishedAt);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name IN :tags")
    List<Post> findPostsByTags(@Param("tags") List<String> tags);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE (:authors IS NULL OR p.author IN :authors) " +
            "AND (:tagIds IS NULL OR t.name IN :tagIds) " +
            "AND (:startDate IS NULL OR p.publishedAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.publishedAt <= :endDate)")
    Page<Post> findFilteredPosts(
            @Param("authors") List<String> authors,
            @Param("tagIds") List<String> tagIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
