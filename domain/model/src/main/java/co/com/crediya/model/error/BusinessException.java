package co.com.crediya.model.error;

public class BusinessException extends RuntimeException {

    public enum Type{
        LOAN_TYPE_NOT_EXISTS("El tipo de prestamo solicitado  (id:%s) no existe"),
        LOAN_NOT_EXISTS("El prestamo a actualizar  (id:%s) no existe"),
        USER_NOT_EXISTS("El usuario con el email indicado no existe"),
        STATUS_TO_UPDATE_NOT_VALID("El estado %s , no es un estado válido para actualizar una solicitud"),
        STATUS_ALREADY_DEFINED("La solicitud a actualizar ya se encuentra en estado %s"),
        NOTIFICATION_SEND_FAILED("El estado de la solicitud se ha actualizado pero no fue posible enviar el correo electronico. Error: %s"),

        INVALID_AMOUNT(BusinessValidations.INVALID_AMOUNT_VALUE),
        INVALID_DEADLINE(BusinessValidations.INVALID_DEADLINE_VALUE);
        
        private final String message;

        public BusinessException build(){
            return new BusinessException(this);
        }

        public BusinessException build(String personalizedMessage){
            String finalMessage = String.format(this.message, personalizedMessage);
            return new BusinessException(finalMessage);
        }

        Type(String message) {
            this.message = message;
        }
    }


    private BusinessException(Type type){
        super(type.message);
    }

    public BusinessException(String personalizedMessage){
        super(personalizedMessage);
    }

}
