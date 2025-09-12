package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.gateway.MessageSerializer;
import co.com.crediya.model.gateway.QueueSenderGateway;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanStatus;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateApplicationStatusUseCase {

    private final LoanApplicationRepository repository;
    private final QueueSenderGateway queueSenderGateway;
    private final MessageSerializer serializer;

    public Mono<LoanApplication> execute(Long applicationId, LoanStatus newStatus) {

        return validateNewStatus(newStatus)
                .flatMap(statusValidated -> repository.getById(applicationId))
                .switchIfEmpty(Mono.error(BusinessException.Type.LOAN_NOT_EXISTS.build()))
                .filter(application -> !application.getStatusId().equals(newStatus.getStatusId()))
                .switchIfEmpty(Mono.error(BusinessException.Type.STATUS_ALREADY_DEFINED.build(newStatus.name())))
                .flatMap(this::validateCurrentStatusApplication)
                .map(application -> application.toBuilder().statusId(newStatus.getStatusId()).build())
                .flatMap(repository::save)
                .flatMap(saved -> queueSenderGateway
                        .send(serializer.toJson(saved))
                        .thenReturn(saved)
                );
    }
    
    private Mono<LoanApplication> validateCurrentStatusApplication(LoanApplication application) {

        if (application.getStatusId().equals(LoanStatus.APPROVED.getStatusId())) {
            return Mono.error(BusinessException.Type.STATUS_ALREADY_DEFINED.build(LoanStatus.APPROVED.name()));

        } else if (application.getStatusId().equals(LoanStatus.REJECTED.getStatusId())) {
            return Mono.error(BusinessException.Type.STATUS_ALREADY_DEFINED.build(LoanStatus.REJECTED.name()));

        }
        return Mono.just(application);
    }

    private Mono<LoanStatus> validateNewStatus(LoanStatus newStatus) {
        if (newStatus.equals(LoanStatus.PENDING) || newStatus.equals(LoanStatus.MANUAL)) {
            return Mono.error(BusinessException.Type.STATUS_TO_UPDATE_NOT_VALID.build(newStatus.name()));
        }
        return Mono.just(newStatus);
    }
}

