package co.com.crediya.api.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        errorAttributes.put("path", request.path());
        errorAttributes.put("errorMessage", error.getMessage());
        String errorCauseMessage = error.getCause() == null ? "Validacion de negocio no superada" : error.getCause().getMessage();
        errorAttributes.put("InitCause", errorCauseMessage);
        return errorAttributes;
    }


}
