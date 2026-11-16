package com.pickit.backend.category.controller;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.category.service.CategoryService;
import com.pickit.backend.common.ApiResponse;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/categories")
    public ApiResponse<List<CategoryResponse>> getCategories(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return ApiResponse.ok(categoryService.getCategories());
    }
}
