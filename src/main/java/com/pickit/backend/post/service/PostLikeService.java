package com.pickit.backend.post.service;

import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostLike;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.repository.PostLikeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final ExchangePostRepository exchangePostRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, ExchangePostRepository exchangePostRepository) {
        this.postLikeRepository = postLikeRepository;
        this.exchangePostRepository = exchangePostRepository;
    }

    @Transactional
    public Long likePost(Long userId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (postLikeRepository.existsByUserIdAndExchangePost(userId, post)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 교환글입니다.");
        }

        try {
            return postLikeRepository.saveAndFlush(new PostLike(userId, post)).getLikeId();
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 UK(user_id, post_id)에 걸린 경우
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 교환글입니다.");
        }
    }
}
