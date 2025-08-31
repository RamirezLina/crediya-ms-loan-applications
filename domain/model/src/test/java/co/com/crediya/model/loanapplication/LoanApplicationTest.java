package co.com.crediya.model.loanapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.error.BusinessValidations;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanApplicationTest {

    @Test
    void validateAmountReturnsErrorWhenNonPositive() {
        LoanApplication app = LoanApplication.builder()
                .amount(0)
                .deadline(LocalDate.now().plusDays(1))
                .build();

        StepVerifier.create(app.validateAmount())
                .expectErrorSatisfies(throwable -> {
                    assertEquals(BusinessException.class, throwable.getClass());
                    assertEquals(BusinessValidations.INVALID_AMOUNT_VALUE, throwable.getMessage());
                })
                .verify();
    }

    @Test
    void validateAmountReturnsMonoWhenPositive() {
        LoanApplication app = LoanApplication.builder()
                .amount(1000)
                .deadline(LocalDate.now().plusDays(1))
                .build();

        StepVerifier.create(app.validateAmount())
                .expectNextMatches(next -> next == app)
                .verifyComplete();
    }

    @Test
    void validateDeadlineReturnsErrorWhenNotAfterToday() {
        LoanApplication appToday = LoanApplication.builder()
                .amount(100)
                .deadline(LocalDate.now())
                .build();

        StepVerifier.create(appToday.validateDeadline())
                .expectErrorSatisfies(throwable -> {
                    assertEquals(BusinessException.class, throwable.getClass());
                    assertEquals(BusinessValidations.INVALID_DEADLINE_VALUE, throwable.getMessage());
                })
                .verify();

        LoanApplication appPast = LoanApplication.builder()
                .amount(100)
                .deadline(LocalDate.now().minusDays(1))
                .build();

        StepVerifier.create(appPast.validateDeadline())
                .expectErrorSatisfies(throwable -> {
                    assertEquals(BusinessException.class, throwable.getClass());
                    assertEquals(BusinessValidations.INVALID_DEADLINE_VALUE, throwable.getMessage());
                })
                .verify();
    }

    @Test
    void validateDeadlineReturnsMonoWhenAfterToday() {
        LoanApplication app = LoanApplication.builder()
                .amount(100)
                .deadline(LocalDate.now().plusDays(2))
                .build();

        StepVerifier.create(app.validateDeadline())
                .expectNextMatches(next -> next == app)
                .verifyComplete();
    }

    @Test
    void defineApplicationStatusSetsPending() {
        LoanApplication app = LoanApplication.builder()
                .amount(100)
                .deadline(LocalDate.now().plusDays(1))
                .statusId(null)
                .build();

        app.defineApplicationStatus();

        assertEquals(LoanStatus.PENDING.getStatusId(), app.getStatusId());
    }
}

