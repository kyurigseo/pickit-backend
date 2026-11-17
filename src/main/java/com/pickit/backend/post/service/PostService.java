package com.pickit.backend.post.service;

import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.repository.PostLikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final ExchangePostRepository exchangePostRepository;
    private final PostLikeRepository postLikeRepository;

    public PostService(ExchangePostRepository exchangePostRepository, PostLikeRepository postLikeRepository) {
        this.exchangePostRepository = exchangePostRepository;
        this.postLikeRepository = postLikeRepository;
    }

    public PostListResponse getPosts(Long popupId, Long categoryId, String status, String sort, String keyword, int page, int size, Long currentUserId) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("RECOMMEND".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<ExchangePost> postPage = exchangePostRepository.findPosts(popupId, categoryId, status, keyword, pageable);

        var content = postPage.getContent().stream()
                .map(post -> {
                    boolean isLiked = currentUserId != null && postLikeRepository.existsByUserIdAndExchangePost(currentUserId, post);
                    return PostListResponse.PostSummary.from(post, isLiked);
                })
                .toList();

        return new PostListResponse(content, postPage.getNumber(), postPage.getSize());
    }
}
