package co.com.crediya.model.loanapplication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanStatusTest {

    @Test
    void statusIdsMatchSpecification() {
        assertEquals(1L, LoanStatus.PENDING.getStatusId());
        assertEquals(2L, LoanStatus.APPROVED.getStatusId());
        assertEquals(3L, LoanStatus.REJECTED.getStatusId());
    }
}

