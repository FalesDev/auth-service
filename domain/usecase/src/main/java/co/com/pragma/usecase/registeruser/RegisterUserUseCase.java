package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exception.EmailAlreadyExistsException;
import co.com.pragma.model.exception.EntityNotFoundException;
import co.com.pragma.model.gateways.PasswordEncoder;
import co.com.pragma.model.gateways.TransactionalGateway;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TransactionalGateway transactionalGateway;

    public Mono<User> registerUser(User user) {
        return transactionalGateway.executeInTransaction(
                userRepository.existsByEmail(user.getEmail())
                        .flatMap(exists -> exists
                                ? Mono.error(new EmailAlreadyExistsException("Email is already registered"))
                                : roleRepository.findByName("CLIENT")
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Role not found")))
                                .flatMap(role -> {
                                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                                    user.setIdRole(role.getId());
                                    return userRepository.save(user);
                                })
                        )
        );
    }
}
