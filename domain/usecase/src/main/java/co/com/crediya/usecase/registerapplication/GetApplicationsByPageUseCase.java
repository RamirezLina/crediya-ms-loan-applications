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
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class GetApplicationsByPageUseCase {

    private final LoanApplicationRepository applicationRepository;
    private final UserRepository userRepository;


    public Mono<LoanApplicationPage> getLoanApplicationsByPage(int page, int size, String token) {
        AtomicReference<User> lastUserRef = new AtomicReference<>();
        return applicationRepository.getByPage(page, size, LoanStatus.APPROVED.getStatusId())
                .flatMap(pageResult -> getMissingContentData(pageResult, token, lastUserRef));
    }


    private Mono<LoanApplicationPage> getMissingContentData(LoanApplicationPage page, 
                                                            String token, AtomicReference<User> lastUserRef) {
        return Flux.fromIterable(page.getContent())
                .flatMap(item -> getUserData(item, token, lastUserRef))
                .map(appPage -> appPage.toBuilder()
                        .monthlyAmount(appPage.calculateMonthlyAmount())
                        .build())
                .collectList()
                .map(list -> page.toBuilder().content(list).build());
    }

    private Mono<LoanApplicationForPage> getUserData(LoanApplicationForPage loanApplicationForPage, 
                                                     String token, AtomicReference<User> lastUserRef) {
        User cached = lastUserRef.get();
        if (Objects.nonNull(cached) && cached.getEmail().equals(loanApplicationForPage.getEmail())) {
            return Mono.just(completeUserData(loanApplicationForPage, cached));
        }
        return userRepository.getUserByEmail(loanApplicationForPage.getEmail(), token)
                .doOnNext(lastUserRef::set)
                .map(user -> completeUserData(loanApplicationForPage, user));
    }

    private LoanApplicationForPage completeUserData(LoanApplicationForPage application, User newData) {
        return application.toBuilder()
                .name(newData.getName())
                .baseSalary(newData.getBaseSalary())
                .build();
    }

   

}
