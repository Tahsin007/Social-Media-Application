package com.example.social_media_application.post.repository;

import com.example.social_media_application.auth.model.User;
import com.example.social_media_application.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Get all public posts and current user's private posts
    @Query("SELECT p FROM Post p WHERE p.isPublic = true OR p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> findAllVisiblePosts(@Param("userId") Long userId, Pageable pageable);

    // Get posts by user
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Get public posts only
    Page<Post> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
}
