package co.com.crediya.model.loantype;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {

    private Long id;
    private String name;
    private double minAmount;
    private double maxAmount;
    private double interestRate;
    private boolean automaticValidation;
}
