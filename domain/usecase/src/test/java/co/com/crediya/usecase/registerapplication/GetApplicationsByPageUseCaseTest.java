package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.loanapplication.LoanApplicationForPage;
import co.com.crediya.model.loanapplication.LoanApplicationPage;
import co.com.crediya.model.loanapplication.LoanStatus;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetApplicationsByPageUseCaseTest {

    @Mock
    private LoanApplicationRepository applicationRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetApplicationsByPageUseCase useCase;

    private final String token = "token-123";

    private LoanApplicationForPage baseItem(String email) {
        return LoanApplicationForPage.builder()
                .id(1L)
                .email(email)
                .totalAmount(1200.0)
                .interestRate(0.12)
                .creationDate(LocalDate.of(2024, 1, 1))
                .deadline(LocalDate.of(2024, 7, 1))
                .loanType("Consumo")
                .loanStatus("APPROVED")
                .build();
    }

    private User user(String email, String name, double baseSalary) {
        return User.builder()
                .email(email)
                .name(name)
                .baseSalary(baseSalary)
                .build();
    }

    @BeforeEach
    void resetMocks() {
        clearInvocations(applicationRepository, userRepository);
    }

    @Test
    void enrichesPageAndCachesUserByEmail() {
        LoanApplicationForPage a1 = baseItem("a@ex.com");
        LoanApplicationForPage a2 = baseItem("a@ex.com");
        LoanApplicationPage page = LoanApplicationPage.builder()
                .content(List.of(a1, a2))
                .elementsInPage(2)
                .pageNumber(0)
                .totalElements(2)
                .pageSize(2)
                .totalPages(1)
                .build();

        when(applicationRepository.getByPage(anyInt(), anyInt(), eq(LoanStatus.APPROVED.getStatusId())))
                .thenReturn(Mono.just(page));
        when(userRepository.getUserByEmail(eq("a@ex.com"), eq(token)))
                .thenReturn(Mono.just(user("a@ex.com", "Ana", 3_000_000.0)));

        StepVerifier.create(useCase.getLoanApplicationsByPage(0, 2, token))
                .assertNext(out -> {
                    assertEquals(2, out.getContent().size());
                    // Both items should be enriched with the same user data and monthly amount
                    out.getContent().forEach(item -> {
                        assertEquals("Ana", item.getName());
                        assertEquals(3_000_000.0, item.getBaseSalary());
                        assertEquals(207.06, item.getMonthlyAmount());
                    });
                })
                .verifyComplete();

        verify(applicationRepository, times(1)).getByPage(0, 2, LoanStatus.APPROVED.getStatusId());
        verify(userRepository, times(1)).getUserByEmail("a@ex.com", token); // cached for second
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void fetchesUserForDifferentEmails() {
        LoanApplicationForPage a1 = baseItem("a@ex.com");
        LoanApplicationForPage a2 = baseItem("b@ex.com");
        LoanApplicationPage page = LoanApplicationPage.builder()
                .content(List.of(a1, a2))
                .elementsInPage(2)
                .pageNumber(0)
                .totalElements(2)
                .pageSize(2)
                .totalPages(1)
                .build();

        when(applicationRepository.getByPage(anyInt(), anyInt(), eq(LoanStatus.APPROVED.getStatusId())))
                .thenReturn(Mono.just(page));
        when(userRepository.getUserByEmail(eq("a@ex.com"), eq(token)))
                .thenReturn(Mono.just(user("a@ex.com", "Ana", 3_000_000.0)));
        when(userRepository.getUserByEmail(eq("b@ex.com"), eq(token)))
                .thenReturn(Mono.just(user("b@ex.com", "Beto", 2_500_000.0)));

        StepVerifier.create(useCase.getLoanApplicationsByPage(0, 2, token))
                .assertNext(out -> {
                    assertEquals("Ana", out.getContent().get(0).getName());
                    assertEquals(3_000_000.0, out.getContent().get(0).getBaseSalary());
                    assertEquals("Beto", out.getContent().get(1).getName());
                    assertEquals(2_500_000.0, out.getContent().get(1).getBaseSalary());
                })
                .verifyComplete();

        verify(userRepository, times(1)).getUserByEmail("a@ex.com", token);
        verify(userRepository, times(1)).getUserByEmail("b@ex.com", token);
    }
}

