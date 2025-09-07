package co.com.crediya.r2dbc.helper;

public class PersonalizedQuery {

    public static final String APPLICATION_PAGEABLE_DATA = """
     SELECT
                     s.id_solicitud AS id,
                     s.email AS email,
                     s.monto AS amount,
                     s.plazo AS deadline,
                     s.fecha_creacion AS creationdate,
                     t.nombre AS loantype,
                     t.tasa_interes AS rate,
                     e.descripcion AS loanstatus
                 FROM solicitud s
                 JOIN tipo_prestamo t ON t.id_tipo_prestamo = s.id_tipo_prestamo
                 JOIN estados e ON e.id_estado = s.id_estado
                 WHERE e.id_estado != :status
                 ORDER BY s.email
                 LIMIT :limit OFFSET :offset
                 
""";

    public static final String APPLICATION_PAGEABLE_COUNT = """
        SELECT COUNT(*)
        FROM solicitud s
        JOIN estados e ON e.id_estado = s.id_estado
        WHERE e.id_estado != :status
    """;

    private PersonalizedQuery() {
    }
}
