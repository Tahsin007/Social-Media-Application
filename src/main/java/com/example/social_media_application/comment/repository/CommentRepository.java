package com.example.social_media_application.comment.repository;

import com.example.social_media_application.comment.model.Comment;
import com.example.social_media_application.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Get all top-level comments (no parent) for a post
    List<Comment> findByPostAndParentCommentIsNullOrderByCreatedAtAsc(Post post);

    // Get all replies for a specific comment
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parentComment);

    // Get all comments for a post (including replies)
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    // Count comments for a post
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    long countByPost(@Param("post") Post post);
}
