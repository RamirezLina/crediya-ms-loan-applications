package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanStatus;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterApplicationUseCase {

    private final LoanApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserRepository userRepository;
    private final CalculateCapacityUseCase calculateCapacityUseCase;

    public Mono<LoanApplication> execute(LoanApplication newApplication, String token) {
        return newApplication.validateAmount()
                .then(newApplication.validateDeadline())
                .flatMap(validatedApplication ->
                        Mono.zip(
                                setLoanApplicationStatus(validatedApplication),
                                userRepository.existUserByEmail(validatedApplication.getEmail(), token)))
                .flatMap(zip -> zip.getT2()
                        ? applicationRepository.save(zip.getT1())
                        : Mono.error(BusinessException.Type.USER_NOT_EXISTS.build())
                )
                .flatMap(savedApplication -> processIfAutomaticStatus(token, savedApplication));
    }

    private Mono<LoanApplication> processIfAutomaticStatus(String token, LoanApplication savedApplication) {
        return savedApplication.getStatusId().equals(LoanStatus.AUTOMATIC.getStatusId())
                ? calculateCapacityUseCase.execute(savedApplication, token).thenReturn(savedApplication)
                : Mono.just(savedApplication);
    }

    private Mono<LoanApplication> setLoanApplicationStatus(LoanApplication validatedApplication) {
        return loanTypeRepository.findById(validatedApplication.getLoanTypeId())
                .switchIfEmpty(Mono.error(BusinessException.Type.LOAN_TYPE_NOT_EXISTS.build(
                        validatedApplication.getLoanTypeId().toString())))
                .map(validatedApplication::defineApplicationStatus);
    }

}
