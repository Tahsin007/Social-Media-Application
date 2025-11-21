package com.example.social_media_application.comment.repository;

import com.example.social_media_application.auth.model.User;
import com.example.social_media_application.comment.model.Comment;
import com.example.social_media_application.comment.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    boolean existsByCommentAndUser(Comment comment, User user);
    List<CommentLike> findByComment(Comment comment);
    void deleteByCommentAndUser(Comment comment, User user);
}
