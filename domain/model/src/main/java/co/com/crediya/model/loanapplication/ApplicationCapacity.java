package co.com.crediya.model.loanapplication;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationCapacity {
    Long idApplication;
    private String email;
    private String name;
    private Double baseSalary;
    private Double interestRate;
    private LocalDate creationDate;
    private LocalDate deadline;
    private Double totalAmount;
    private List<ApprovedApplication> approvedApplicationList;
}
