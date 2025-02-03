package mate.academy.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {
    private String message;
    private Long id;
    private String email;
}
