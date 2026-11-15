package com.pickit.backend.popup.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PopupListResponse(
        List<PopupListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public static PopupListResponse from(Page<PopupListItemResponse> items) {
        return new PopupListResponse(
                items.getContent(),
                items.getNumber(),
                items.getSize(),
                items.getTotalElements(),
                items.getTotalPages(),
                items.hasNext()
        );
    }
}
