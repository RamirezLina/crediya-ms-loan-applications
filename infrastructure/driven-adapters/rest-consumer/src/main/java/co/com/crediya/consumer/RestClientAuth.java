package co.com.crediya.consumer;

import co.com.crediya.model.error.AppException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientAuth implements UserRepository {
    private final WebClient client;

    @CircuitBreaker(name = "existUserByEmail")
    public Mono<Boolean> existUserByEmail(String email, String token) {
        log.info("[CREATE LOAN APPLICATION]  Validando la existencia de un usuario con este email");
        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/email/{email}").build(email))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        this::handleServerError
                )
                .bodyToMono(Boolean.class);
    }

    public  Mono<User> getUserByEmail(String email, String token) {
        log.info("Obteniendo la informacion del usuario en ms-auth");
        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/detail/{email}").build(email))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        this::handleClientError
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        this::handleServerError
                )
                .bodyToMono(User.class);
    }

    private Mono<Throwable> handleClientError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(AppException.Type.MS_REQUEST_400_ERROR::build);

    }

    private Mono<Throwable> handleServerError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(AppException.Type.MS_REQUEST_500_ERROR::build);
    }
}
