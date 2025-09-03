package co.com.crediya.api;

import co.com.crediya.api.config.LoanApplicationPath;
import co.com.crediya.api.docs.OpenApiControllerDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest implements OpenApiControllerDoc {
    
    private final LoanApplicationPath loanApplicationPath;
    private final LoanApplicationHandler handler;
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler loanApplicationHandler) {
        return route(POST( loanApplicationPath.getLoanApp()), loanApplicationHandler::listenRegisterLoanApplication);
    }
}
