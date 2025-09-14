package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.loanapplication.LoanApplicationPage;
import co.com.crediya.model.loanapplication.LoanApplicationForPage;
import co.com.crediya.r2dbc.entity.PageableApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationMapper {

    public LoanApplicationPage toModel(Page<PageableApplicationEntity> page){
        List<LoanApplicationForPage> content = page.getContent().stream()
            .map(this::toModel)
            .toList();
        return LoanApplicationPage.builder()
            .content(content)
            .elementsInPage(page.getNumberOfElements())
            .pageNumber(page.getNumber())
            .totalElements( page.getTotalElements())
            .pageSize(page.getSize())
            .totalPages(page.getTotalPages())
            .build();
    }
    
    public LoanApplicationForPage toModel(PageableApplicationEntity entity) {
        return LoanApplicationForPage.builder()
                .id(entity.id())
                .email(entity.email())
                .totalAmount(entity.amount())
                .deadline(entity.deadline())
                .interestRate(entity.rate())
                .loanType(entity.loantype())
                .loanStatus(entity.loanstatus())
                .creationDate(entity.creationdate())
                .build();
    }
}
