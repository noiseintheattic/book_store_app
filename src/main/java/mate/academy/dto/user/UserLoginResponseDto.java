package mate.academy.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginResponseDto(
        @NotEmpty(message = "Token must not be empty.")
        String token) {
}
