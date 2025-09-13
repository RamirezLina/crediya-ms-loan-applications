package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.api.dto.UpdateApplicationDto;
import co.com.crediya.api.mapper.LoanApplicationDtoMapper;
import co.com.crediya.api.security.UserAccessControl;
import co.com.crediya.usecase.registerapplication.GetApplicationsByPageUseCase;
import co.com.crediya.usecase.registerapplication.RegisterApplicationUseCase;
import co.com.crediya.usecase.registerapplication.UpdateApplicationStatusUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationHandler {

    private final RegisterApplicationUseCase registerApplicationUseCase;
    private final GetApplicationsByPageUseCase getApplicationsByPageUseCase;
    private final UpdateApplicationStatusUseCase updateApplicationStatusUseCase;

    private final LoanApplicationDtoMapper mapper;
    private final Validator validator;
    private final UserAccessControl userAccessControl;


    @PreAuthorize("hasAuthority('USER')")
    public Mono<ServerResponse> listenRegisterLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationDto.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("El cuerpo de la solicitud es requerido")))
                .flatMap(this::validateDto)
                .doOnNext(dto -> log.info("[REGISTER LOAN APPLICATION] Se inicia el registro de la solicitud de prestamo"))
                .flatMap(dto -> userAccessControl.validateResourceOwnership(dto, serverRequest))
                .map(mapper::toModel)
                .flatMap(model -> registerApplicationUseCase.execute(model, getToken(serverRequest)))
                .map(mapper::toDto)
                .flatMap(applicationDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(applicationDto))
                .doOnError(this::logError);
    }

    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> listenGetApplicationsToReview(ServerRequest serverRequest) {
        return Mono.just(serverRequest.queryParam("page"))
                .zipWith(Mono.just(serverRequest.queryParam("size")))
                .doOnNext(t -> log.info("[GET APPLICATIONS TO REVIEW] Se inica la busqueda de solicitudes por revisar: Pag {}, Registros/pag: {}", t.getT1(), t.getT2()))
                .flatMap(tuple -> getApplicationsByPageUseCase.getLoanApplicationsByPage(
                        getIntFromOptional(tuple.getT1(), "page"),
                        getIntFromOptional(tuple.getT2(), "size"),
                        getToken(serverRequest)
                ))
                .flatMap(page -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(page))
                .doOnError(this::logError);
    }

    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> listenUpdateLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateApplicationDto.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("El cuerpo de la solicitud es requerido")))
                .flatMap(this::validateDto)
                .doOnNext(dto -> log.info("[UPDATE LOAN APPLICATION] Se inicia el la actualizacion a status {}", dto.newStatus()))
                .flatMap(dto -> updateApplicationStatusUseCase.execute(
                        dto.applicationId(), dto.newStatus()))
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

    private Mono<UpdateApplicationDto> validateDto(UpdateApplicationDto dto) {
        Set<ConstraintViolation<UpdateApplicationDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(dto);
    }

    private int getIntFromOptional(Optional<String> optional, String paramName) {
        return optional.map(value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new ServerWebInputException("El parámetro '" + paramName + "' debe ser un número entero válido.");
            }
        }).orElseThrow(() -> new ServerWebInputException("El parámetro '" + paramName + "' es requerido."));
    }


    private void logError(Throwable exception) {
        log.error("Error Message: {} \n Stack trace: {}", exception.getMessage(), exception.getStackTrace());
    }
}
