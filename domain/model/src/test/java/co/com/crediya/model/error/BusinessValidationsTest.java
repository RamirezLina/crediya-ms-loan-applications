package co.com.crediya.model.error;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class BusinessValidationsTest {

    @Test
    void privateConstructorIsInvokableViaReflection() throws Exception {
        Constructor<BusinessValidations> ctor = BusinessValidations.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        BusinessValidations instance = ctor.newInstance();
        assertNotNull(instance);
        assertFalse(BusinessValidations.INVALID_AMOUNT_VALUE.isEmpty());
        assertFalse(BusinessValidations.INVALID_DEADLINE_VALUE.isEmpty());
    }
}

