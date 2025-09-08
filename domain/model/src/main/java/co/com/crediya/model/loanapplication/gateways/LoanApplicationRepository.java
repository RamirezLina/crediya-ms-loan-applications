package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationPage;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication application);

    Mono<LoanApplicationPage> getByPage(int page, int size, Long status);
}
