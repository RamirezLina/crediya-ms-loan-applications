package co.com.crediya.consumer;

import co.com.crediya.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestClientAuth implements UserRepository {
    private final WebClient client;
    
    @CircuitBreaker(name = "existUserByEmail", fallbackMethod = "testGetUsers")
    public Mono<Boolean> existUserByEmail(String email) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/email/{email}").build(email))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<String> testGetUsers(Exception ignored) {
        return client
                .get() 
                .retrieve()
                .bodyToMono(String.class);
    }
    
}
