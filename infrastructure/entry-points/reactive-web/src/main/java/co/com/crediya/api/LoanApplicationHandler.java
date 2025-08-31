package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.api.mapper.LoanApplicationDtoMapper;
import co.com.crediya.usecase.registerapplication.RegisterApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Set;

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
                .map(mapper::toModel)
                .flatMap(registerApplicationUseCase::execute)
                .map(mapper::toDto)
                .flatMap(applicationDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(applicationDto))
                .doOnError(this::logError);
    }

    private Mono<LoanApplicationDto> validateDto(LoanApplicationDto dto) {
        Set<ConstraintViolation<LoanApplicationDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(dto);

    }

    private void logError(Throwable exception) {
        log.error("Error Message: {} \n Stack trace: {}", exception.getMessage(), exception.getStackTrace());
    }
}
