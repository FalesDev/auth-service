package co.com.pragma.security.adapter;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDetailsAdapter {

    private final RoleRepository roleRepository;

    public Mono<UserDetails> toUserDetails(User user) {
        return roleRepository.findById(user.getIdRole())
                .map(role -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_" + role.getName())
                        .build()
                );
    }
}
