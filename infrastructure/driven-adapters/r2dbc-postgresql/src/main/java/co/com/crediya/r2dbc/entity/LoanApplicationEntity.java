package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(value = "solicitud")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanApplicationEntity {

    @Id
    @Column("id_solicitud")
    private Long id;
    @Column("email")
    private String email;
    @Column("monto")
    private double amount;
    @Column("plazo")
    private LocalDate deadline;
    @Column("id_estado")
    private Long statusId;
    @Column("id_tipo_prestamo")
    private Long loanTypeId;
}
