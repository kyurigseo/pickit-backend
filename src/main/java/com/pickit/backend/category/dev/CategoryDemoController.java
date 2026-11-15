package com.pickit.backend.category.dev;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.category.service.CategoryService;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("popup-list-demo")
@RestController
public class CategoryDemoController {

    private final CategoryService categoryService;

    public CategoryDemoController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/dev/categories")
    public List<CategoryResponse> getCategories() {
        return categoryService.getCategories();
    }
}
