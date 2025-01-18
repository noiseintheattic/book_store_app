package mate.academy.dto.category;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CategoryDto {
    private Long id;
    @NotNull
    private String name;
    private String description;
    private List<String> categories;
}
