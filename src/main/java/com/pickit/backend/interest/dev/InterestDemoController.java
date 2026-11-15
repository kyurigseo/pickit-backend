package com.pickit.backend.interest.dev;

import com.pickit.backend.interest.dto.request.InterestUpdateRequest;
import com.pickit.backend.interest.dto.response.InterestResponse;
import com.pickit.backend.interest.service.UserInterestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Profile("popup-list-demo")
@RestController
public class InterestDemoController {

    private final UserInterestService userInterestService;

    public InterestDemoController(UserInterestService userInterestService) {
        this.userInterestService = userInterestService;
    }

    @GetMapping("/api/dev/me/interests")
    public InterestResponse getInterests(@RequestHeader("X-Dev-User-Id") @Positive Long userId) {
        return userInterestService.getInterests(userId);
    }

    @PutMapping("/api/dev/me/interests")
    public InterestResponse updateInterests(@RequestHeader("X-Dev-User-Id") @Positive Long userId,
            @Valid @RequestBody InterestUpdateRequest request) {
        return userInterestService.updateInterests(userId, request.categoryIds());
    }
}
