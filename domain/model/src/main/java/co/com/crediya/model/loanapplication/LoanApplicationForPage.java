package co.com.crediya.model.loanapplication;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationForPage {
    
    private Long id;
    private String email;
    private String name;
    private Double totalAmount;
    private LocalDate deadline;
    private String loanType;
    private Double interestRate;
    private String loanStatus;
    private Double baseSalary;
    private Double monthlyAmount;
    private LocalDate creationDate;
    
}
