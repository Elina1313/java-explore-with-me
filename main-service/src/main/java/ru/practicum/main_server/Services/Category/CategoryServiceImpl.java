package ru.practicum.main_server.Services.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_server.dtos.category.CategoryDto;
import ru.practicum.main_server.dtos.category.NewCategoryDto;
import ru.practicum.main_server.exceptions.CategoryIsNotEmptyException;
import ru.practicum.main_server.exceptions.CategoryNameAlreadyExistException;
import ru.practicum.main_server.exceptions.CategoryNotExistException;
import ru.practicum.main_server.exceptions.CategoryNotFoundException;
import ru.practicum.main_server.mappers.CategoryMapper;
import ru.practicum.main_server.models.Category;
import ru.practicum.main_server.repositories.CategoryRepository;
import ru.practicum.main_server.repositories.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new CategoryNameAlreadyExistException("Category name is already exist");
        }
        return categoryMapper.toCategoryDto(categoryRepository
                .save(categoryMapper.newCategoryDtoToCategory(newCategoryDto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryIsNotEmptyException("Category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("No category with such id " + catId));
        if (categoryRepository.existsByName(categoryDto.getName()) && !category.getName().equals(categoryDto.getName())) {
            throw new CategoryNameAlreadyExistException("Category name is already exist");
        }

        categoryDto.setId(catId);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto)));
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {

        return categoryMapper.toCategoryDtoList(categoryRepository.findAll(pageable).toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotExistException("Category is not exist"));
        return categoryMapper.toCategoryDto(category);
    }
}
