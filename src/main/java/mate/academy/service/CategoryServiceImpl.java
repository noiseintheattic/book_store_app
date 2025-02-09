package mate.academy.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.category.CategoryDto;
import mate.academy.exceptions.DataProcessingException;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category categoryById = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with given id: " + id));
        return categoryMapper.toDto(categoryById);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        try {
            categoryRepository.save(category);
        } catch (RuntimeException e) {
            throw new DataProcessingException("Can't create category id: " + categoryDto.getId()
                    + " with name: " + categoryDto.getName(), e);
        }
        return categoryDto;
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category categoryFromId = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with id: " + id));
        categoryMapper.updateFromDto(categoryDto, categoryFromId);
        Category updatedCategory = categoryRepository.save(categoryFromId);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Can't find category with given id: " + id, e);
        }
    }

    @Override
    public Set<Category> getOrCreateCategories(List<String> categoryNames) {
        return categoryNames.stream()
                .map(name -> categoryRepository.findByName(name)
                        .orElseGet(() -> categoryRepository.save(new Category(name))))
                .collect(Collectors.toSet());
    }
}
