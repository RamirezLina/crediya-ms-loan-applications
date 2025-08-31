package co.com.crediya.api.dto;

import co.com.crediya.model.error.BusinessValidations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record LoanApplicationDto(

        @NotBlank(message = BusinessValidations.INVALID_EMAIL)
        @Email (message = BusinessValidations.INVALID_EMAIL_FORMAT)
        String email,
        
        @NotNull(message = BusinessValidations.INVALID_AMOUNT)
        @Positive (message = BusinessValidations.INVALID_AMOUNT_VALUE) 
        double amount,
        
        @NotNull (message = BusinessValidations.INVALID_DEADLINE)
        @Future (message = BusinessValidations.INVALID_DEADLINE_VALUE)
        LocalDate deadline,

        Long statusId,
        
        @NotNull (message = BusinessValidations.TYPE_NULL)
        Long loanTypeId
) {
}
