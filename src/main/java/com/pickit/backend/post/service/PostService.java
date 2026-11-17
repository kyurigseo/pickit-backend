package com.pickit.backend.post.service;

import com.pickit.backend.interest.repository.UserInterestRepository;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.repository.PostLikeRepository;
import java.util.List;
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
    private final UserInterestRepository userInterestRepository;

    public PostService(ExchangePostRepository exchangePostRepository, PostLikeRepository postLikeRepository, UserInterestRepository userInterestRepository) {
        this.exchangePostRepository = exchangePostRepository;
        this.postLikeRepository = postLikeRepository;
        this.userInterestRepository = userInterestRepository;
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

    public List<PostListResponse.PostSummary> getRecommendedPosts(Long userId, int size) {
        List<Long> categoryIds = userInterestRepository.findCategoryIdsByUserId(userId);
        Pageable pageable = PageRequest.of(0, size);

        List<ExchangePost> posts;
        if (categoryIds == null || categoryIds.isEmpty()) {
            posts = exchangePostRepository.findByStatusOpen(pageable);
        } else {
            posts = exchangePostRepository.findByCategoryIdInAndStatusOpen(categoryIds, pageable);
            if (posts.isEmpty()) {
                posts = exchangePostRepository.findByStatusOpen(pageable);
            }
        }

        return posts.stream()
                .map(post -> PostListResponse.PostSummary.from(post, false))
                .toList();
    }
}
