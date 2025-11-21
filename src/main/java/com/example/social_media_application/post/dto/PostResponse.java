package com.example.social_media_application.post.dto;

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
public class PostResponse {
    private Long id;
    private UserResponse user;
    private String content;
    private String imageUrl;
    private Boolean isPublic;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLikedByCurrentUser;
    private List<UserResponse> likedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
