package co.com.crediya.r2dbc.entity;

import java.time.LocalDate;

public record PageableApplicationEntity(

        Long id,
        String email,
        Double amount,
        LocalDate deadline,
        Double rate,
        String loantype,
        String loanstatus,
        LocalDate creationdate
) {
}


