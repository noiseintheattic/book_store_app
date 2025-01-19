package mate.academy.security;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user "
                        + "by email: " + email));
        System.out.println("========================================");
        System.out.println("roles taken from user:");
        System.out.println("User: " + user.getEmail());
        System.out.println("Roles: " + user.getUserRoles());
        System.out.println("========================================");

        Set<Role> roles = roleRepository.findByUsersEmailAndUsersIsDeletedFalse(email);
        System.out.println("========================================");
        System.out.println("roles taken from user through roleRepository:");
        roles.forEach(System.out::println);
        System.out.println("========================================");

        if (roles.isEmpty()) {
            throw new UsernameNotFoundException("User has no roles assigned.");
        }

        return user;
    }
}
