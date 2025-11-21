package com.example.social_media_application.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private String imageUrl;

    private Boolean isPublic = true;
}
