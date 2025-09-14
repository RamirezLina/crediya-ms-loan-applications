package co.com.crediya.model.loanapplication;

import lombok.*;

import java.time.LocalDate;
import java.time.Period;

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

    public double calculateMonthlyAmount() {
        double monthlyRate = this.getInterestRate() / 12;
        Period period = Period.between(this.getCreationDate(), this.getDeadline());
        int months = period.getYears() * 12 + period.getMonths();
        Double p = this.getTotalAmount();

        return Math.round(
                p * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
                        (Math.pow(1 + monthlyRate, months) - 1) * 100.0
        ) / 100.0;
    }
}
