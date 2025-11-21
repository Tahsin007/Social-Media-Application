package com.example.social_media_application.post.service;

import com.example.social_media_application.auth.dto.UserResponse;
import com.example.social_media_application.auth.model.User;
import com.example.social_media_application.auth.repository.UserRepository;
import com.example.social_media_application.exception.ResourceNotFoundException;
import com.example.social_media_application.exception.UnauthorizedException;
import com.example.social_media_application.post.dto.PostRequest;
import com.example.social_media_application.post.dto.PostResponse;
import com.example.social_media_application.post.model.Post;
import com.example.social_media_application.post.model.PostLike;
import com.example.social_media_application.post.repository.PostLikeRepository;
import com.example.social_media_application.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(PostRequest request, String userEmail) {
        log.info("Creating new post for user: {}", userEmail);

        User user = getUserByEmail(userEmail);

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        post = postRepository.save(post);
        log.info("Post created with ID: {}", post.getId());

        return mapToPostResponse(post, user);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(String userEmail, int page, int size) {
        log.info("Fetching posts for user: {}, page: {}, size: {}", userEmail, page, size);

        User currentUser = getUserByEmail(userEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> posts = postRepository.findAllVisiblePosts(currentUser.getId(), pageable);

        return posts.map(post -> mapToPostResponse(post, currentUser));
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, String userEmail) {
        log.info("Fetching post with ID: {} for user: {}", postId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this post");
        }

        return mapToPostResponse(post, currentUser);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request, String userEmail) {
        log.info("Updating post with ID: {} for user: {}", postId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to update this post");
        }

        post.setContent(request.getContent());
        if (request.getImageUrl() != null) {
            post.setImageUrl(request.getImageUrl());
        }
        if (request.getIsPublic() != null) {
            post.setIsPublic(request.getIsPublic());
        }

        post = postRepository.save(post);
        log.info("Post updated successfully: {}", postId);

        return mapToPostResponse(post, currentUser);
    }

    @Transactional
    public void deletePost(Long postId, String userEmail) {
        log.info("Deleting post with ID: {} for user: {}", postId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this post");
        }

        postRepository.delete(post);
        log.info("Post deleted successfully: {}", postId);
    }

    @Transactional
    public PostResponse toggleLike(Long postId, String userEmail) {
        log.info("Toggling like for post: {} by user: {}", postId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to like this post");
        }

        boolean exists = postLikeRepository.existsByPostAndUser(post, currentUser);

        if (exists) {
            postLikeRepository.deleteByPostAndUser(post, currentUser);
            log.info("Post unliked: {}", postId);
        } else {
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(currentUser)
                    .build();
            postLikeRepository.save(postLike);
            log.info("Post liked: {}", postId);
        }

        post = postRepository.findById(postId).orElseThrow();
        return mapToPostResponse(post, currentUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getPostLikes(Long postId, String userEmail) {
        log.info("Fetching likes for post: {}", postId);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view likes for this post");
        }

        List<PostLike> likes = postLikeRepository.findByPost(post);

        return likes.stream()
                .map(like -> mapToUserResponse(like.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(String userEmail, int page, int size) {
        log.info("Fetching posts for user: {}", userEmail);

        User user = getUserByEmail(userEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return posts.map(post -> mapToPostResponse(post, user));
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        boolean isLikedByCurrentUser = postLikeRepository.existsByPostAndUser(post, currentUser);

        List<UserResponse> likedBy = postLikeRepository.findByPost(post).stream()
                .map(like -> mapToUserResponse(like.getUser()))
                .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .user(mapToUserResponse(post.getUser()))
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .isPublic(post.getIsPublic())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .likedBy(likedBy)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
