package co.com.crediya.usecase.registerapplication;


import co.com.crediya.model.loanapplication.LoanApplicationForPage;
import co.com.crediya.model.loanapplication.LoanApplicationPage;
import co.com.crediya.model.loanapplication.LoanStatus;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Period;
import java.util.Objects;

@RequiredArgsConstructor
public class GetApplicationsByPageUseCase {

    private final LoanApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private User currentUserData;
    private String token;


    public Mono<LoanApplicationPage> getLoanApplicationsByPage(int page, int size, String token) {
        this.token = token;
        return applicationRepository.getByPage(page, size, LoanStatus.APPROVED.getStatusId())
                .flatMap(this::getMissingContentData);
    }


    private Mono<LoanApplicationPage> getMissingContentData(LoanApplicationPage page) {
        return Flux.fromIterable(page.getContent())
                .flatMap(this::getUserData)
                .map(appPage -> appPage.toBuilder()
                        .monthlyAmount(calculateMonthlyAmount(appPage))
                        .build())
                .collectList()
                .map(list -> page.toBuilder().content(list).build());
    }

    private Mono<LoanApplicationForPage> getUserData(LoanApplicationForPage loanApplicationForPage) {
        if (Objects.nonNull(currentUserData) && currentUserData.getEmail().equals(loanApplicationForPage.getEmail())) {
            return Mono.just(completeUserData(loanApplicationForPage, currentUserData));
        }
        return userRepository.getUserByEmail(loanApplicationForPage.getEmail(), token)
                .doOnNext(user -> this.currentUserData = user)
                .map(user -> completeUserData(loanApplicationForPage, user));
    }

    private LoanApplicationForPage completeUserData(LoanApplicationForPage application, User newData) {
        return application.toBuilder()
                .name(newData.getName())
                .baseSalary(newData.getBaseSalary())
                .build();
    }

    private double calculateMonthlyAmount(LoanApplicationForPage application) {
        double monthlyRate = application.getInterestRate() / 12;
        Period period = Period.between(application.getCreationDate(), application.getDeadline());
        int months = period.getYears() * 12 + period.getMonths();
        Double p = application.getTotalAmount();

        return Math.round(
                p * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
                        (Math.pow(1 + monthlyRate, months) - 1) * 100.0
        ) / 100.0;
    }

}
