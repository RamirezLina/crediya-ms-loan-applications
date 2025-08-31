package co.com.crediya.r2dbc;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.repository.ApplicationReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@Transactional
@Slf4j
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Long,
        ApplicationReactiveRepository
        > implements LoanApplicationRepository {
    
    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper) {
              super(repository, mapper, applicationEntity -> mapper.map(applicationEntity, LoanApplication.class));
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication application) {
        log.info("[CREATE LOAN APPLICATION] Guardando solicitud de prestamo en base de datos");
        return super.save(application)
                .doOnError(super::logError);

    }


}
