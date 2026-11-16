package com.pickit.backend.popup.controller;

import com.pickit.backend.popup.dto.response.PopupListItemResponse;
import com.pickit.backend.popup.service.PopupLikeService;
import com.pickit.backend.verification.auth.VerificationUserResolver;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<PopupListItemResponse> getLikedPopups(Principal principal) {
        return popupLikeService.getLikedPopups(userResolver.requireUserId(principal));
    }
}
