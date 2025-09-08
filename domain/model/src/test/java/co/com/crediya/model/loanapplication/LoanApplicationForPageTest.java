package co.com.crediya.model.loanapplication;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanApplicationForPageTest {

    @Test
    void builderSetsAllFields() {
        LocalDate created = LocalDate.of(2024, 1, 1);
        LocalDate deadline = LocalDate.of(2024, 7, 1);

        LoanApplicationForPage item = LoanApplicationForPage.builder()
                .id(10L)
                .email("a@ex.com")
                .name("Name")
                .totalAmount(1200.0)
                .deadline(deadline)
                .loanType("Consumo")
                .interestRate(0.12)
                .loanStatus("APPROVED")
                .baseSalary(3_000_000.0)
                .monthlyAmount(207.07)
                .creationDate(created)
                .build();

        assertEquals(10L, item.getId());
        assertEquals("a@ex.com", item.getEmail());
        assertEquals("Name", item.getName());
        assertEquals(1200.0, item.getTotalAmount());
        assertEquals(deadline, item.getDeadline());
        assertEquals("Consumo", item.getLoanType());
        assertEquals(0.12, item.getInterestRate());
        assertEquals("APPROVED", item.getLoanStatus());
        assertEquals(3_000_000.0, item.getBaseSalary());
        assertEquals(207.07, item.getMonthlyAmount());
        assertEquals(created, item.getCreationDate());
    }

    @Test
    void toBuilderCopiesAndAllowsChanges() {
        LoanApplicationForPage base = LoanApplicationForPage.builder()
                .id(1L)
                .email("b@ex.com")
                .name("Base")
                .totalAmount(1000.0)
                .loanType("Libre")
                .interestRate(0.1)
                .loanStatus("PENDING")
                .baseSalary(2_000_000.0)
                .build();

        LoanApplicationForPage modified = base.toBuilder()
                .name("Changed")
                .monthlyAmount(123.45)
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("b@ex.com", modified.getEmail());
        assertEquals("Changed", modified.getName());
        assertEquals(1000.0, modified.getTotalAmount());
        assertEquals("Libre", modified.getLoanType());
        assertEquals(0.1, modified.getInterestRate());
        assertEquals("PENDING", modified.getLoanStatus());
        assertEquals(2_000_000.0, modified.getBaseSalary());
        assertEquals(123.45, modified.getMonthlyAmount());
        assertNull(modified.getDeadline());
    }
}

