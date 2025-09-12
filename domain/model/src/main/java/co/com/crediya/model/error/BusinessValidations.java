package co.com.crediya.model.error;


public class BusinessValidations {
    public static final String INVALID_EMAIL = "El correo electronico no puede ser nulo o vacio";
    public static final String INVALID_EMAIL_FORMAT = "El correo electronico debe tener un formato válido";
    public static final String INVALID_DEADLINE = "La fecha de plazo de la solicitud de prestamo no puede sr nulo o vacio";
    public static final String INVALID_DEADLINE_VALUE = "La fecha de plazo de la solicitud de prestamo no es despues del dia de hoy";
    public static final String INVALID_AMOUNT = "La cantidad del prestamo solicitado no puede ser nula";
    public static final String INVALID_AMOUNT_VALUE = "La cantidad del prestamo solicitado no puede ser negativo o 0";
    public static final String TYPE_NULL= "El tipo de prestamo no puede ser nulo o vacio";
    public static final String STATUS_NULL= "El estado de prestamo a actualizar no puede ser nulo o vacio";
    public static final String INVALID_STATUS_VALUE= "El estado de prestamo a actualizar no puede ser negativo";
    public static final String ID_NULL= "El ID del prestamo a actualizar no puede ser nulo o vacio";
    public static final String INVALID_ID_VALUE= "El ID del prestamo a actualizar no puede ser negativo";
   
    private BusinessValidations() {
    }
}

