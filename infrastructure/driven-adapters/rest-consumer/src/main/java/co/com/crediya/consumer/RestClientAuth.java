package co.com.crediya.consumer;

import co.com.crediya.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientAuth implements UserRepository {
    private final WebClient client;

    @CircuitBreaker(name = "existUserByEmail", fallbackMethod = "fallbackExistUserByEmail")
    public Mono<Boolean> existUserByEmail(String email, String token) {
        log.info("[CREATE LOAN APPLICATION]  Validando la existencia de un usuario con este email");
        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/email/{email}").build(email))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    private Mono<Boolean> fallbackExistUserByEmail(String email, String token) {
        return Mono.just(false);
    }
}
