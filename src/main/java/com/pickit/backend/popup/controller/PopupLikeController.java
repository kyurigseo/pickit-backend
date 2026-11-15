package com.pickit.backend.popup.controller;

import com.pickit.backend.popup.dto.response.PopupListResponse;
import com.pickit.backend.popup.service.PopupLikeService;
import com.pickit.backend.verification.auth.VerificationUserResolver;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PopupLikeController {

    private final PopupLikeService popupLikeService;
    private final VerificationUserResolver userResolver;

    public PopupLikeController(PopupLikeService popupLikeService, VerificationUserResolver userResolver) {
        this.popupLikeService = popupLikeService;
        this.userResolver = userResolver;
    }

    @GetMapping("/api/me/liked-popups")
    public PopupListResponse getLikedPopups(
            Principal principal,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return popupLikeService.getLikedPopups(userResolver.requireUserId(principal), page, size);
    }
}
