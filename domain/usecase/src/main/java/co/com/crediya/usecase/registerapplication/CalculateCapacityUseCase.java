package co.com.crediya.usecase.registerapplication;


import co.com.crediya.model.gateway.MessageSerializer;
import co.com.crediya.model.gateway.QueueSenderGateway;
import co.com.crediya.model.loanapplication.*;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CalculateCapacityUseCase {

    private final LoanApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final QueueSenderGateway queueSenderGateway;
    private final MessageSerializer serializer;

    public Mono<Void> execute(LoanApplication application, String token) {
        return Mono.zip(
                        loanTypeRepository.findById(application.getLoanTypeId())
                                .map(LoanType::getInterestRate),
                        applicationRepository.getApprovedByUser(
                                        application.getEmail(), LoanStatus.APPROVED.getStatusId())
                                .map(this::mapToApprovedApplication)
                                .collectList(),
                        userRepository.getUserByEmail(application.getEmail(), token))
                .map(zip -> mapToApplicationCapacityDto(application, zip.getT1(), zip.getT2(), zip.getT3()))
                .flatMap(completeDto -> queueSenderGateway
                        .sendToCapacity(serializer.toJson(completeDto)))
                .then();
    }

    private ApprovedApplication mapToApprovedApplication(LoanApplicationForPage applicationForPage) {
        return new ApprovedApplication(
                applicationForPage.getId(),
                applicationForPage.calculateMonthlyAmount()
        );
    }

    private ApplicationCapacity mapToApplicationCapacityDto(LoanApplication application, double interestRate,
                                                            List<ApprovedApplication> approvedApplications, User user) {
        return ApplicationCapacity.builder()
                .idApplication(application.getId())
                .email(application.getEmail())
                .interestRate(interestRate)
                .name(user.getName())
                .baseSalary(user.getBaseSalary())
                .creationDate(LocalDate.now())
                .deadline(application.getDeadline())
                .totalAmount(application.getAmount())
                .approvedApplicationList(approvedApplications)
                .build();
    }
    
}
