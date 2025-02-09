package mate.academy.service;

import mate.academy.dto.category.CategoryDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;



    @Test
    @DisplayName("Verify if exception will be thrown with non existing"
            + "category")
    void getById_WithNonExisingCategory_ShouldThrowException() {
        // Given
        Long categoryId = 100L;
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );

        // Then
        String expected = "Can't find category with given id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Verify if correct category will be returned"
            + "if given correct category id.")
    void getById_WithValidCategory_ShouldReturnCategory() {
        // Given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Horror");
        category.setDescription("Scary books.");

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));
        CategoryDto categoryDto = categoryService.getById(categoryId);


        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );

        // Then
        String expected = "Can't find category with given id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Verify save() method works.")
    void save_WithValidCategoryRequestDto_ShouldReturnCategoryDto() {
        // Given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Horror");
        categoryDto.setDescription("Scary books.");

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName("Horror");
        category.setDescription("Scary books.");
        Mockito.when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(category);

        // When
        CategoryDto savedCategoryDto = categoryService.save(categoryDto);

        // Then
        assertEquals(categoryDto, savedCategoryDto);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works.")
    public void findAll_ValidPageable_ReturnsAllCategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Horror");
        category.setDescription("Scary books.");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, 5);

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        List<CategoryDto> categoriesDto = categoryService.findAll(pageable);

        // Then
        assertEquals(1,categoriesDto.size());
        assertEquals(categoryDto,categoriesDto.get(0));

        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(category);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);


    }


}