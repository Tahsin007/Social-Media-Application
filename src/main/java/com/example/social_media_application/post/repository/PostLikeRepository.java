package com.example.social_media_application.post.repository;

import com.example.social_media_application.auth.model.User;
import com.example.social_media_application.post.model.Post;
import com.example.social_media_application.post.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    List<PostLike> findByPost(Post post);
    void deleteByPostAndUser(Post post, User user);
}
