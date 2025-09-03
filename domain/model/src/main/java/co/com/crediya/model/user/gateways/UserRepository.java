package co.com.crediya.model.user.gateways;

import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<Boolean> existUserByEmail(String email, String token);
}
