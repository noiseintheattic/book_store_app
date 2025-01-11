package mate.academy.service;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exceptions.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with given email:"
                    + requestDto.getEmail() + " already exist.");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        User savedUser = userRepository.save(user);
        UserResponseDto userResponseDto = userMapper.toUserResponseDto(savedUser);
        userResponseDto.setMessage("You have been successfully registered.");
        return userResponseDto;
    }
}
