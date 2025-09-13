package co.com.crediya.api.dto;

import co.com.crediya.model.error.BusinessValidations;
import co.com.crediya.model.loanapplication.LoanStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateApplicationDto(

        @NotNull(message = BusinessValidations.ID_NULL)
        Long applicationId,

        @NotNull(message = BusinessValidations.STATUS_NULL)
        LoanStatus newStatus
) {
}
