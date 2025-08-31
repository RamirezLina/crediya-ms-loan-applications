package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoanApplicationDto;
import co.com.crediya.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationDtoMapper {
    
    LoanApplication toModel(LoanApplicationDto dto);
    LoanApplicationDto toDto(LoanApplication user);
    
}
