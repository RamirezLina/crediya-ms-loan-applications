package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.error.BusinessValidations;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RegisterApplicationUseCaseTest {

    @Mock
    private  LoanApplicationRepository applicationRepository;
    @Mock
    private  LoanTypeRepository loanTypeRepository;
    @Mock
    private  UserRepository userRepository;
    
    @InjectMocks
    private RegisterApplicationUseCase underTest;
    
    private LoanApplication application;
    private final String token = "dummy-token";


    @BeforeEach
    void setUp() {
        application = LoanApplication.builder()
                .email("email@example")
                .amount(1000.5)
                .deadline(LocalDate.now().plusDays(3))
                .loanTypeId(1L)
                .build();
    }
    
    @Test
    void registerLoanApplication_success(){
        LoanType type = mock(LoanType.class);
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.just(type));
        when(userRepository.existUserByEmail(any(String.class), any(String.class))).thenReturn(Mono.just(true));
        when(applicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(application));

        StepVerifier.create(underTest.execute(application, token))
                .expectNext(application)
                .verifyComplete();
        
        verify(loanTypeRepository, times(1))
                .findById(application.getLoanTypeId());
        verify(userRepository, times(1))
                .existUserByEmail(application.getEmail(), token);
        
      ArgumentCaptor<LoanApplication> captor = ArgumentCaptor.forClass(LoanApplication.class);
      verify(applicationRepository, times(1)).save(captor.capture());
      LoanApplication captured = captor.getValue();
      assertEquals(application.getEmail(), captured.getEmail());
      assertEquals(application.getAmount(), captured.getAmount());
      assertEquals(application.getDeadline(), captured.getDeadline());
      assertEquals(application.getLoanTypeId(), captured.getLoanTypeId());
      assertEquals(1L, captured.getStatusId());
    }

    @Test
    void registerLoanApplication_shouldReturnError_whenUserIsInvalid(){
        application.setAmount(-2000);
        
        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        BusinessValidations.INVALID_AMOUNT_VALUE.equals(ex.getMessage()))
                .verify();

        verifyNoInteractions(loanTypeRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldReturnError_whenDeadlineInvalidToday() {
        application.setDeadline(LocalDate.now());

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        BusinessValidations.INVALID_DEADLINE_VALUE.equals(ex.getMessage()))
                .verify();

        verifyNoInteractions(loanTypeRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldReturnError_whenLoanTypeNotExists() {
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.empty());

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ("El tipo de prestamo solicitado  (id:" + application.getLoanTypeId() + ") no existe").equals(ex.getMessage()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(application.getLoanTypeId());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldReturnError_whenUserNotExists() {
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.just(mock(LoanType.class)));
        when(userRepository.existUserByEmail(any(String.class), any(String.class))).thenReturn(Mono.just(false));

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        "El usuario con el email indicado no existe".equals(ex.getMessage()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(application.getLoanTypeId());
        verify(userRepository, times(1)).existUserByEmail(application.getEmail(), token);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldPropagateError_whenLoanTypeRepositoryFails() {
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.error(new RuntimeException("loanTypeFail")));

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        "loanTypeFail".equals(ex.getMessage()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(application.getLoanTypeId());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldPropagateError_whenUserRepositoryFails() {
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.just(mock(LoanType.class)));
        when(userRepository.existUserByEmail(any(String.class), any(String.class))).thenReturn(Mono.error(new RuntimeException("userRepoFail")));

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        "userRepoFail".equals(ex.getMessage()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(application.getLoanTypeId());
        verify(userRepository, times(1)).existUserByEmail(application.getEmail(), token);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void registerLoanApplication_shouldPropagateError_whenApplicationRepositoryFails() {
        when(loanTypeRepository.findById(any(Long.class))).thenReturn(Mono.just(mock(LoanType.class)));
        when(userRepository.existUserByEmail(any(String.class), any(String.class))).thenReturn(Mono.just(true));
        when(applicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.error(new RuntimeException("saveFail")));

        StepVerifier.create(underTest.execute(application, token))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        "saveFail".equals(ex.getMessage()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(application.getLoanTypeId());
        verify(userRepository, times(1)).existUserByEmail(application.getEmail(), token);
        verify(applicationRepository, times(1)).save(any(LoanApplication.class));
    }

}
