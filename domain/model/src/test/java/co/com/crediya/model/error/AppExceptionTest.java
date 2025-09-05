package co.com.crediya.model.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppExceptionTest {

    @Test
    void build400FormatsMessageWithDetail() {
        AppException ex = AppException.Type.MS_REQUEST_400_ERROR.build("detalle-400");
        // Nota: el mensaje base incluye un espacio al final tras %s
        assertEquals("La peticion al microservicio de autenticacion retorno un error 400: detalle-400 ", ex.getMessage());
    }

    @Test
    void build500FormatsMessageWithDetail() {
        AppException ex = AppException.Type.MS_REQUEST_500_ERROR.build("detalle-500");
        // Nota: el mensaje base incluye un espacio al final tras %s
        assertEquals("La peticion al microservicio de autenticacion retorno un error 500: detalle-500 ", ex.getMessage());
    }

    @Test
    void buildWithTypeUsesPredefinedPattern() {
        AppException ex = AppException.Type.MS_REQUEST_400_ERROR.build();
        assertEquals("La peticion al microservicio de autenticacion retorno un error 400: %s ", ex.getMessage());
    }

    @Test
    void publicConstructorSetsCustomMessage() {
        AppException ex = new AppException("mensaje-app");
        assertEquals("mensaje-app", ex.getMessage());
    }
}

