package co.com.crediya.usecase.registerapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.loanapplication.LoanApplication;
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


    public Mono<LoanApplication> execute(LoanApplication newApplication, String token) {
        return newApplication.validateAmount()
                .then(newApplication.validateDeadline())
                .map(LoanApplication::defineApplicationStatus)
                .flatMap(validatedApplication -> loanTypeRepository.findById(validatedApplication.getLoanTypeId()))
                .switchIfEmpty(Mono.error(BusinessException.Type.LOAN_TYPE_NOT_EXISTS.build(newApplication.getLoanTypeId().toString())))
                .flatMap(loanType -> userRepository.existUserByEmail(newApplication.getEmail(), token))
                .flatMap(exists -> exists
                        ? applicationRepository.save(newApplication)
                        : Mono.error(BusinessException.Type.USER_NOT_EXISTS.build())
                );
    }
}
