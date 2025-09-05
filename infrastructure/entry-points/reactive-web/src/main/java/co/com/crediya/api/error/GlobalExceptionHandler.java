package co.com.crediya.api.error;

import co.com.crediya.model.error.AuthException;
import co.com.crediya.model.error.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final Map<Class<? extends Throwable>, HttpStatus> statusByException =
            Map.of(
                    BusinessException.class, HttpStatus.BAD_REQUEST,
                    ConstraintViolationException.class, HttpStatus.BAD_REQUEST,
                    ServerWebInputException.class, HttpStatus.BAD_REQUEST,
                    AuthException.class, HttpStatus.FORBIDDEN
            );


    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                  ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);

        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }


    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Throwable error = getError(serverRequest);
        HttpStatus status = defineHttpStatus((Exception) error);

        Map<String, Object> errorProperties = getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults());

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorProperties));
    }

    private HttpStatus defineHttpStatus(Exception exception) {
        return statusByException.getOrDefault(
                exception.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
