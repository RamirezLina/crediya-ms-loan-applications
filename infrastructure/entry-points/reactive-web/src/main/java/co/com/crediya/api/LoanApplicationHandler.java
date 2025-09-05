package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.api.mapper.LoanApplicationDtoMapper;
import co.com.crediya.model.error.AuthException;
import co.com.crediya.usecase.registerapplication.RegisterApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Set;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationHandler {

    private final RegisterApplicationUseCase registerApplicationUseCase;
    private final LoanApplicationDtoMapper mapper;
    private final Validator validator;


    public Mono<ServerResponse> listenRegisterLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDto.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("El cuerpo de la solicitud es requerido")))
                .flatMap(this::validateDto)
                .doOnNext(dto -> log.info("[REGISTER LOAN APPLICATION] Se inicia el registro de la solicitud de prestamo"))
                .flatMap(dto -> validateUserRequest(dto, serverRequest))
                .map(mapper::toModel)
                .flatMap(model -> registerApplicationUseCase.execute(model, getToken(serverRequest)))
                .map(mapper::toDto)
                .flatMap(applicationDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(applicationDto))
                .doOnError(this::logError);
    }

    private String getToken(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
    }

    private Mono<LoanApplicationDto> validateDto(LoanApplicationDto dto) {
        Set<ConstraintViolation<LoanApplicationDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(dto);
    }

    private Mono<String> getTokenEmail(ServerRequest serverRequest) {
        return serverRequest.principal()
                .switchIfEmpty(Mono.error(AuthException.Type.TOKEN_NOT_FOUND
                        .build("No se pudo obtener el principal de seguridad")))
                .mapNotNull(principal -> {
                    if (principal instanceof JwtAuthenticationToken jwtAuth) {
                        Object subClaim = jwtAuth.getToken().getClaims().get("sub");
                        return subClaim != null ? subClaim.toString() : null;
                    }
                    return null;
                })
                .switchIfEmpty(Mono.error(AuthException.Type.TOKEN_NOT_FOUND
                        .build("No se pudo obtener el principal de seguridad")));
    }

    private Mono<LoanApplicationDto> validateUserRequest(LoanApplicationDto dto, ServerRequest serverRequest) {
        return getTokenEmail(serverRequest)
                .zipWith(Mono.just(dto.email()))
                .flatMap(tuple -> {
                    String emailFromToken = tuple.getT1();
                    String emailFromBody = tuple.getT2();
                    if (!emailFromToken.equals(emailFromBody)) {
                        return Mono.error(AuthException.Type.USER_ACCESS_DENIED.build());
                    }
                    return Mono.just(dto);
                });
    }
    
    private void logError(Throwable exception) {
        log.error("Error Message: {} \n Stack trace: {}", exception.getMessage(), exception.getStackTrace());
    }
}
