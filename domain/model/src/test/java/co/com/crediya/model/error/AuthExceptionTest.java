package co.com.crediya.model.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthExceptionTest {

    @Test
    void buildUserAccessDeniedUsesFixedMessage() {
        AuthException ex = AuthException.Type.USER_ACCESS_DENIED.build();
        assertEquals("El email del token no coincide con el email a quien se solicita el prestamo", ex.getMessage());
    }

    @Test
    void buildTokenNotFoundFormatsDetail() {
        AuthException ex = AuthException.Type.TOKEN_NOT_FOUND.build("no-decoded");
        assertEquals("No se logro extraer la informacion del token: no-decoded", ex.getMessage());
    }

    @Test
    void publicConstructorSetsCustomMessage() {
        AuthException ex = new AuthException("mensaje-auth");
        assertEquals("mensaje-auth", ex.getMessage());
    }
}

