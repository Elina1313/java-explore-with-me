package ru.practicum.main_server.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main_server.dtos.category.CategoryDto;
import ru.practicum.main_server.dtos.category.NewCategoryDto;
import ru.practicum.main_server.models.Category;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    Category categoryDtoToCategory(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> list);
}
