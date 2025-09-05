package co.com.crediya.model.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void buildWithTypeUsesPredefinedMessage() {
        BusinessException ex = BusinessException.Type.INVALID_AMOUNT.build();
        assertEquals(BusinessValidations.INVALID_AMOUNT_VALUE, ex.getMessage());
    }

    @Test
    void buildWithFormattedMessageReplacesPlaceholder() {
        BusinessException ex = BusinessException.Type.LOAN_TYPE_NOT_EXISTS.build("42");
        assertEquals("El tipo de prestamo solicitado  (id:42) no existe", ex.getMessage());
    }

    @Test
    void buildWithFixedMessageType() {
        BusinessException ex = BusinessException.Type.USER_NOT_EXISTS.build();
        assertEquals("El usuario con el email indicado no existe", ex.getMessage());
    }

    @Test
    void publicConstructorSetsCustomMessage() {
        BusinessException ex = new BusinessException("Mensaje personalizado");
        assertEquals("Mensaje personalizado", ex.getMessage());
    }
}

