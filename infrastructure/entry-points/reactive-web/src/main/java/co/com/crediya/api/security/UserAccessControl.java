package co.com.crediya.api.security;

import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.model.error.AuthException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class UserAccessControl {

    public Mono<LoanApplicationDto> validateResourceOwnership(LoanApplicationDto dto, ServerRequest serverRequest) {
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

    

}
