package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "tipo_prestamo")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanTypeEntity {

    @Id
    @Column("id_tipo_prestamo")
    private Long id;
    @Column("nombre")
    private String name;
    @Column("monto_minimo")
    private double minAmount;
    @Column("monto_maximo")
    private double maxAmount;
    @Column("tasa_interes")
    private double interestRate;
    @Column("validacion_automatica")
    private boolean automaticValidation;
}
