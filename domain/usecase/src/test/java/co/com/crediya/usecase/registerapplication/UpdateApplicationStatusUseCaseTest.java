package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.gateway.MessageSerializer;
import co.com.crediya.model.gateway.QueueSenderGateway;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanStatus;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationStatusUseCaseTest {

    @Mock
    private LoanApplicationRepository repository;
    @Mock
    private QueueSenderGateway queueSenderGateway;
    @Mock
    private MessageSerializer serializer;

    @InjectMocks
    private UpdateApplicationStatusUseCase useCase;

    private final Long applicationId = 99L;

    private LoanApplication applicationWithStatus(LoanStatus status) {
        return LoanApplication.builder()
                .id(applicationId)
                .email("user@example.com")
                .amount(1000)
                .loanTypeId(1L)
                .statusId(status.getStatusId())
                .build();
    }

    @BeforeEach
    void reset() {
        clearInvocations(repository, queueSenderGateway, serializer);
    }

    @Test
    void updateSuccessFromPendingToApprovedAndSendsMessage() {
        LoanApplication found = applicationWithStatus(LoanStatus.PENDING);
        LoanApplication saved = found.toBuilder().statusId(LoanStatus.APPROVED.getStatusId()).build();

        when(repository.getById(eq(applicationId))).thenReturn(Mono.just(found));
        when(repository.save(any(LoanApplication.class))).thenReturn(Mono.just(saved));
        when(serializer.toJson(eq(saved))).thenReturn("{json}");
        when(queueSenderGateway.send(eq("{json}"))).thenReturn(Mono.just("ok"));

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.APPROVED))
                .expectNext(saved)
                .verifyComplete();

        verify(repository, times(1)).getById(applicationId);

        ArgumentCaptor<LoanApplication> toSaveCaptor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(repository, times(1)).save(toSaveCaptor.capture());
        LoanApplication toSave = toSaveCaptor.getValue();
        assertEquals(LoanStatus.APPROVED.getStatusId(), toSave.getStatusId());

        verify(serializer, times(1)).toJson(saved);
        verify(queueSenderGateway, times(1)).send("{json}");
        verifyNoMoreInteractions(queueSenderGateway);
    }

    @Test
    void errorWhenNewStatusIsInvalidPending() {
        StepVerifier.create(useCase.execute(applicationId, LoanStatus.PENDING))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("no es un estado válido"))
                .verify();

        verifyNoInteractions(repository);
        verifyNoInteractions(queueSenderGateway);
        verifyNoInteractions(serializer);
    }

    @Test
    void errorWhenApplicationNotFound() {
        when(repository.getById(eq(applicationId))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.APPROVED))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("El prestamo a actualizar"))
                .verify();

        verify(repository, times(1)).getById(applicationId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(queueSenderGateway);
        verifyNoInteractions(serializer);
    }

    @Test
    void errorWhenNewStatusEqualsCurrent() {
        when(repository.getById(eq(applicationId))).thenReturn(Mono.just(applicationWithStatus(LoanStatus.APPROVED)));

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.APPROVED))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("ya se encuentra en estado APPROVED"))
                .verify();

        verify(repository, times(1)).getById(applicationId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(queueSenderGateway);
        verifyNoInteractions(serializer);
    }

    @Test
    void errorWhenCurrentStatusApprovedAndTryingToChange() {
        when(repository.getById(eq(applicationId))).thenReturn(Mono.just(applicationWithStatus(LoanStatus.APPROVED)));

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.REJECTED))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("ya se encuentra en estado APPROVED"))
                .verify();

        verify(repository, times(1)).getById(applicationId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(queueSenderGateway);
        verifyNoInteractions(serializer);
    }

    @Test
    void errorWhenCurrentStatusRejectedAndTryingToChange() {
        when(repository.getById(eq(applicationId))).thenReturn(Mono.just(applicationWithStatus(LoanStatus.REJECTED)));

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.APPROVED))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("ya se encuentra en estado REJECTED"))
                .verify();

        verify(repository, times(1)).getById(applicationId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(queueSenderGateway);
        verifyNoInteractions(serializer);
    }

    @Test
    void propagateErrorWhenQueueSendFailsAfterSave() {
        LoanApplication found = applicationWithStatus(LoanStatus.PENDING);
        LoanApplication saved = found.toBuilder().statusId(LoanStatus.REJECTED.getStatusId()).build();

        when(repository.getById(eq(applicationId))).thenReturn(Mono.just(found));
        when(repository.save(any(LoanApplication.class))).thenReturn(Mono.just(saved));
        when(serializer.toJson(eq(saved))).thenReturn("{json}");
        when(queueSenderGateway.send(eq("{json}"))).thenReturn(Mono.error(new RuntimeException("queueFail")));

        StepVerifier.create(useCase.execute(applicationId, LoanStatus.REJECTED))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        "queueFail".equals(ex.getMessage()))
                .verify();

        verify(repository, times(1)).getById(applicationId);
        verify(repository, times(1)).save(any(LoanApplication.class));
        verify(serializer, times(1)).toJson(saved);
        verify(queueSenderGateway, times(1)).send("{json}");
    }
}

