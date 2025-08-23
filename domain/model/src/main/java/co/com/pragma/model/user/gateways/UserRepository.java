package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);
    Mono<User> update(User user);
    Flux<User> findAll();
    Mono<User> findById(String id);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByEmail(String email);
}
