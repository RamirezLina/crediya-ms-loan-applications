package co.com.crediya.model.loanapplication;

import co.com.crediya.model.error.BusinessException;
import co.com.crediya.model.loantype.LoanType;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

    private Long id;
    private String email;
    private double amount;
    private LocalDate deadline;
    private Long statusId;
    private Long loanTypeId;
    private LocalDate creationDate;


    public Mono<LoanApplication> validateAmount() {
        if (this.amount <= 0) {
            return Mono.error(BusinessException.Type.INVALID_AMOUNT.build());
        }
        return Mono.just(this);
    }

    public Mono<LoanApplication> validateDeadline() {
        if (!this.deadline.isAfter(LocalDate.now())) {
            return Mono.error(BusinessException.Type.INVALID_DEADLINE.build());
        }
        return Mono.just(this);
    }

    public LoanApplication defineApplicationStatus(LoanType type) {
        this.setStatusId(type.isAutomaticValidation()
                ? LoanStatus.AUTOMATIC.getStatusId()
                : LoanStatus.MANUAL.getStatusId());
        return this;
    }


}
