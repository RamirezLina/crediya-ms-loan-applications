package co.com.crediya.model.loanapplication;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationPage {
    
    private List<LoanApplicationForPage> content;
    private int elementsInPage;
    private int pageNumber;
    private long totalElements;
    private int pageSize;
    private int totalPages;
}
