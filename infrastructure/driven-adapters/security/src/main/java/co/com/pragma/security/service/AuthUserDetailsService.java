package co.com.pragma.security.service;

import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.security.adapter.UserDetailsAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final UserDetailsAdapter userDetailsAdapter;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .flatMap(userDetailsAdapter::toUserDetails);
    }
}
