package co.com.crediya.model.loantype;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanTypeTest {

    @Test
    void builderSetsAllFields() {
        LoanType type = LoanType.builder()
                .id(5L)
                .name("Consumo")
                .minAmount(1000)
                .maxAmount(5000)
                .interestRate(1.2)
                .automaticValidation(true)
                .build();

        assertEquals(5L, type.getId());
        assertEquals("Consumo", type.getName());
        assertEquals(1000, type.getMinAmount());
        assertEquals(5000, type.getMaxAmount());
        assertEquals(1.2, type.getInterestRate());
        assertTrue(type.isAutomaticValidation());
    }

    @Test
    void toBuilderCopiesAndAllowsChanges() {
        LoanType base = LoanType.builder()
                .id(1L)
                .name("Original")
                .minAmount(100)
                .maxAmount(200)
                .interestRate(0.5)
                .automaticValidation(false)
                .build();

        LoanType modified = base.toBuilder()
                .name("Modificado")
                .maxAmount(300)
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("Modificado", modified.getName());
        assertEquals(100, modified.getMinAmount());
        assertEquals(300, modified.getMaxAmount());
        assertEquals(0.5, modified.getInterestRate());
        assertEquals(false, modified.isAutomaticValidation());
    }
}

