package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.PersonalizedQuery;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query(PersonalizedQuery.APPLICATION_PAGEABLE_COUNT)
    Mono<Long> countAllByStatusNot(Long status);
}
