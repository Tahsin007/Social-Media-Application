package com.example.social_media_application.post.controller;

import com.example.social_media_application.auth.dto.UserResponse;
import com.example.social_media_application.post.dto.PostRequest;
import com.example.social_media_application.post.dto.PostResponse;
import com.example.social_media_application.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.createPost(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        Page<PostResponse> responses = postService.getAllPosts(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.getPostById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostResponse> toggleLike(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.toggleLike(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<UserResponse>> getPostLikes(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        List<UserResponse> responses = postService.getPostLikes(id, userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<Page<PostResponse>> getMyPosts(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        Page<PostResponse> responses = postService.getUserPosts(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(responses);
    }
}
