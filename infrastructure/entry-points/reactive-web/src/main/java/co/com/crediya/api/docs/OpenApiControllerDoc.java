package co.com.crediya.api.docs;

import co.com.crediya.api.LoanApplicationHandler;
import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.api.error.ErrorPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


public interface OpenApiControllerDoc {

    @RouterOperations({
            @RouterOperation(path = "/api/v1/solicitud",
                    produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST, beanClass = LoanApplicationHandler.class, beanMethod = "listenRegisterLoanApplication",
                    operation = @Operation(operationId = "RegisterLoanApplication",
                            summary = "Registrar una nueva solicitud de prestamo",
                            tags = {"API Solicitudes"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Datos de la solicitud de prestamo",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoanApplicationDto.class)
                                    )
                            ),
                            responses = {@ApiResponse(responseCode = "200", description = "Solicitud de prestamo registrada.",
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanApplicationDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))),
                                    @ApiResponse(responseCode = "401", description = "No autenticado",
                                            content = @Content(mediaType = "application/json")),
                                    @ApiResponse(responseCode = "403", description = "Acceso prohibido: el usuario no esta autorizado",
                                            content = @Content(mediaType = "application/json"))})
            )})
    RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler loanApplicationHandler);

}
