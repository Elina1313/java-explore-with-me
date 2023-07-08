package ru.practicum.main_server.Services.Category;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_server.dtos.category.CategoryDto;
import ru.practicum.main_server.dtos.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addNewCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategories(Pageable pageable);
}
