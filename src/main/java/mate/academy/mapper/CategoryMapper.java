package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.category.CategoryDto;
import mate.academy.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    CategoryDto toDto(Category category);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Category toModel(CategoryDto categoryDto);

    void updateFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
