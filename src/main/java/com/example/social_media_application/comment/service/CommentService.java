package com.example.social_media_application.comment.service;

import com.example.social_media_application.auth.dto.UserResponse;
import com.example.social_media_application.auth.model.User;
import com.example.social_media_application.auth.repository.UserRepository;
import com.example.social_media_application.comment.dto.CommentRequest;
import com.example.social_media_application.comment.dto.CommentResponse;
import com.example.social_media_application.comment.model.Comment;
import com.example.social_media_application.comment.model.CommentLike;
import com.example.social_media_application.comment.repository.CommentLikeRepository;
import com.example.social_media_application.comment.repository.CommentRepository;
import com.example.social_media_application.exception.ResourceNotFoundException;
import com.example.social_media_application.exception.UnauthorizedException;
import com.example.social_media_application.post.model.Post;
import com.example.social_media_application.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, String userEmail) {
        log.info("Creating comment for post: {} by user: {}", postId, userEmail);

        User user = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getIsPublic() && !post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to comment on this post");
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = getCommentOrThrow(request.getParentCommentId());
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to this post");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        comment = commentRepository.save(comment);
        log.info("Comment created with ID: {}", comment.getId());

        return mapToCommentResponse(comment, user);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId, String userEmail) {
        log.info("Fetching comments for post: {}", postId);

        User currentUser = getUserByEmail(userEmail);
        Post post = getPostOrThrow(postId);

        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view comments on this post");
        }

        List<Comment> topLevelComments = commentRepository
                .findByPostAndParentCommentIsNullOrderByCreatedAtAsc(post);

        return topLevelComments.stream()
                .map(comment -> mapToCommentResponseWithReplies(comment, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId, String userEmail) {
        log.info("Fetching comment with ID: {}", commentId);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        Post post = comment.getPost();
        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view this comment");
        }

        return mapToCommentResponseWithReplies(comment, currentUser);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, String userEmail) {
        log.info("Updating comment with ID: {} by user: {}", commentId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to update this comment");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        log.info("Comment updated successfully: {}", commentId);

        return mapToCommentResponse(comment, currentUser);
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        log.info("Deleting comment with ID: {} by user: {}", commentId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", commentId);
    }

    @Transactional
    public CommentResponse toggleLike(Long commentId, String userEmail) {
        log.info("Toggling like for comment: {} by user: {}", commentId, userEmail);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        Post post = comment.getPost();
        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to like this comment");
        }

        boolean exists = commentLikeRepository.existsByCommentAndUser(comment, currentUser);

        if (exists) {
            commentLikeRepository.deleteByCommentAndUser(comment, currentUser);
            log.info("Comment unliked: {}", commentId);
        } else {
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(currentUser)
                    .build();
            commentLikeRepository.save(commentLike);
            log.info("Comment liked: {}", commentId);
        }

        comment = commentRepository.findById(commentId).orElseThrow();
        return mapToCommentResponse(comment, currentUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getCommentLikes(Long commentId, String userEmail) {
        log.info("Fetching likes for comment: {}", commentId);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        Post post = comment.getPost();
        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view likes for this comment");
        }

        List<CommentLike> likes = commentLikeRepository.findByComment(comment);

        return likes.stream()
                .map(like -> mapToUserResponse(like.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse replyToComment(Long commentId, CommentRequest request, String userEmail) {
        log.info("Creating reply to comment: {} by user: {}", commentId, userEmail);

        User user = getUserByEmail(userEmail);
        Comment parentComment = getCommentOrThrow(commentId);
        Post post = parentComment.getPost();

        if (!post.getIsPublic() && !post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to reply to this comment");
        }

        Comment reply = Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();

        reply = commentRepository.save(reply);
        log.info("Reply created with ID: {}", reply.getId());

        return mapToCommentResponse(reply, user);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(Long commentId, String userEmail) {
        log.info("Fetching replies for comment: {}", commentId);

        User currentUser = getUserByEmail(userEmail);
        Comment comment = getCommentOrThrow(commentId);

        Post post = comment.getPost();
        if (!post.getIsPublic() && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You don't have permission to view replies");
        }

        List<Comment> replies = commentRepository.findByParentCommentOrderByCreatedAtAsc(comment);

        return replies.stream()
                .map(reply -> mapToCommentResponse(reply, currentUser))
                .collect(Collectors.toList());
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private CommentResponse mapToCommentResponse(Comment comment, User currentUser) {
        boolean isLikedByCurrentUser = commentLikeRepository.existsByCommentAndUser(comment, currentUser);

        List<UserResponse> likedBy = commentLikeRepository.findByComment(comment).stream()
                .map(like -> mapToUserResponse(like.getUser()))
                .collect(Collectors.toList());

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .user(mapToUserResponse(comment.getUser()))
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .likedBy(likedBy)
                .replies(null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private CommentResponse mapToCommentResponseWithReplies(Comment comment, User currentUser) {
        CommentResponse response = mapToCommentResponse(comment, currentUser);

        List<Comment> replies = commentRepository.findByParentCommentOrderByCreatedAtAsc(comment);
        List<CommentResponse> replyResponses = replies.stream()
                .map(reply -> mapToCommentResponse(reply, currentUser))
                .collect(Collectors.toList());

        response.setReplies(replyResponses);
        return response;
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
