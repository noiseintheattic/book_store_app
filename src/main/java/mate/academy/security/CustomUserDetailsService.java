package mate.academy.security;

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
import org.springframework.transaction.annotation.Transactional;

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

        return user;
    }
}
