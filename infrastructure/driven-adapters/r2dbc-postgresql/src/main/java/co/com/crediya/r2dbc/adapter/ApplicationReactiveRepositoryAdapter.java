package co.com.crediya.r2dbc.adapter;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationForPage;
import co.com.crediya.model.loanapplication.LoanApplicationPage;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.entity.PageableApplicationEntity;
import co.com.crediya.r2dbc.helper.PersonalizedQuery;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.ApplicationMapper;
import co.com.crediya.r2dbc.repository.ApplicationReactiveRepository;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Repository
@Transactional
@Slf4j
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Long,
        ApplicationReactiveRepository
        > implements LoanApplicationRepository {

    private final ApplicationMapper applicationMapper;
    private final DatabaseClient databaseClient;

    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper pageApplicationMapper, ApplicationMapper pageMapper, DatabaseClient databaseClient) {
              super(repository, pageApplicationMapper, applicationEntity -> pageApplicationMapper.map(applicationEntity, LoanApplication.class));
        this.applicationMapper = pageMapper;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication application) {
        log.info("Guardando solicitud de prestamo en base de datos");
        return super.save(application)
                .doOnError(super::logError);
    }

    @Override
    public Mono<LoanApplicationPage> getByPage(int page, int size, Long status) {
        log.info("[GET APPLICATIONS TO REVIEW] Iniciando consulta en base de datos: Pag {}, Size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        return this.findPage(status, size, page * size)
                .collectList()
                .zipWith(repository.countAllByStatusNot(status))
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()))
                .map(applicationMapper::toModel)
                .doOnError(this::logError);
    }

    @Override
    public Mono<LoanApplication> getById(Long applicationId) {
        log.info("[GET APPLICATION BY ID ] Iniciando consulta en base de datos para el id de prestamo {}", applicationId);
        return super.findById(applicationId)
                .filter(Objects::nonNull)
                .doOnError(this::logError);
    }

    @Override
    public Flux<LoanApplicationForPage> getApprovedByUser(String email, Long status) {
        log.info("[GET APPROVED APPLICATIONS BY USER ] Iniciando consulta en base de datos para el usuario {}", email);
        return this.findApplicationsByUser(email, status)
                .map(applicationMapper::toModel)                
                .doOnError(this::logError);
    }

    private Flux<PageableApplicationEntity> findPage(Long statusId, int limit, int offset) {
        return databaseClient.sql(PersonalizedQuery.APPLICATION_PAGEABLE_DATA)
                .bind("status", statusId)
                .bind("limit",  limit)
                .bind("offset", offset)
                .map(getMappingFunction())
                .all();
    }

    private Flux<PageableApplicationEntity> findApplicationsByUser(String email, Long status) {
        return databaseClient.sql(PersonalizedQuery.APPROVED_APPLICATION_BY_USER)
                .bind("email", email)
                .bind("status",  status)
                .map(getMappingFunction())
                .all();
    }

    private BiFunction<Row, RowMetadata, PageableApplicationEntity> getMappingFunction() {
        return (row, meta) -> new PageableApplicationEntity(
                row.get("id", Long.class),
                row.get("email", String.class),
                Optional.ofNullable(row.get("amount", BigDecimal.class))
                        .map(BigDecimal::doubleValue)
                        .orElse(null),
                row.get("deadline", java.time.LocalDate.class),
                Optional.ofNullable(row.get("rate", BigDecimal.class))
                        .map(BigDecimal::doubleValue)
                        .orElse(null),
                row.get("loantype", String.class),
                row.get("loanstatus", String.class),
                row.get("creationdate", java.time.LocalDate.class)
        );
    }
}
