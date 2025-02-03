package mate.academy.service;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.exceptions.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MessageSource messageSource;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            String message = "User with given email:"
                    + requestDto.getEmail() + " already exist.";
            log.error(message);
            throw new RegistrationException(message);
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());

        log.debug("Assigning roles to user with email: {}", requestDto.getEmail());
        user.setUserRoles(Set.of(roleRepository.findById(2L).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find role with id: 2."))));
        log.info("Saving new user with email: {}", requestDto.getEmail());

        User savedUser = userRepository.save(user);
        UserResponseDto userResponseDto = userMapper.toUserResponseDto(savedUser);
        userResponseDto.setMessage("You have been successfully registered.");

        if (shoppingCartRepository.findByUserEmail(requestDto.getEmail()).isEmpty()) {
            log.debug("Creating new shopping cart for user: {}", requestDto.getEmail());
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(savedUser);
            shoppingCartRepository.save(shoppingCart);
        }

        log.info("User with email: {}, has been successfully registered", requestDto.getEmail());
        return userResponseDto;
    }
}
