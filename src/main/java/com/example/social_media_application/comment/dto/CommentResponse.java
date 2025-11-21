package com.example.social_media_application.comment.dto;

import com.example.social_media_application.auth.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long postId;
    private UserResponse user;
    private Long parentCommentId;
    private String content;
    private Integer likeCount;
    private Boolean isLikedByCurrentUser;
    private List<UserResponse> likedBy;
    private List<CommentResponse> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
