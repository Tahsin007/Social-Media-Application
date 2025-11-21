package com.example.social_media_application.comment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private Long parentCommentId;
}