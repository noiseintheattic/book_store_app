package mate.academy.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {
    private String message = "You have been successfully registered.";
    private Long id;
    private String email;
}
