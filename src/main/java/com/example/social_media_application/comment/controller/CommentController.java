package com.example.social_media_application.comment.controller;

import com.example.social_media_application.auth.dto.UserResponse;
import com.example.social_media_application.comment.dto.CommentRequest;
import com.example.social_media_application.comment.dto.CommentResponse;
import com.example.social_media_application.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId,
                                                         @RequestBody CommentRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.createComment(postId, request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsForPost(@PathVariable Long postId,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        List<CommentResponse> responses = commentService.getCommentsByPost(postId, userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.getCommentById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,
                                                         @RequestBody CommentRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.updateComment(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<CommentResponse> toggleLike(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.toggleLike(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/{id}/likes")
    public ResponseEntity<List<UserResponse>> getCommentLikes(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        List<UserResponse> responses = commentService.getCommentLikes(id, userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/comments/{id}/reply")
    public ResponseEntity<CommentResponse> replyToComment(@PathVariable Long id,
                                                          @RequestBody CommentRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.replyToComment(id, request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/comments/{id}/replies")
    public ResponseEntity<List<CommentResponse>> getRepliesToComment(@PathVariable Long id,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        List<CommentResponse> responses = commentService.getReplies(id, userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
}
