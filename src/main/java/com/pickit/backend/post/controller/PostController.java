package com.pickit.backend.post.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.post.dto.response.PostDetailResponse;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.service.PostService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(required = false) Long popupId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "LATEST") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long currentUserId = authentication != null && authentication.getPrincipal() instanceof Long ? (Long) authentication.getPrincipal() : null;
        PostListResponse response = postService.getPosts(popupId, categoryId, status, sort, keyword, page, size, currentUserId);
        return ApiResponse.success(response);
    }

    @GetMapping("/recommended")
    public ApiResponse<List<PostListResponse.PostSummary>> getRecommendedPosts(
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<PostListResponse.PostSummary> response = postService.getRecommendedPosts(userId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long currentUserId = authentication != null && authentication.getPrincipal() instanceof Long ? (Long) authentication.getPrincipal() : null;
        PostDetailResponse response = postService.getPostDetail(postId, currentUserId);
        return ApiResponse.success(response);
    }
}
