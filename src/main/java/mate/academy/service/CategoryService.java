package mate.academy.service;

import java.util.List;
import java.util.Set;
import mate.academy.dto.category.CategoryDto;
import mate.academy.model.Category;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);

    Set<Category> getOrCreateCategories(List<String> categoryNames);
}
