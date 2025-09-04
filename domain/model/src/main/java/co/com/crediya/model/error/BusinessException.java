package co.com.crediya.model.error;

public class BusinessException extends RuntimeException {

    public enum Type{
        LOAN_TYPE_NOT_EXISTS("El tipo de prestamo solicitado  (id:%s) no existe"),
        USER_NOT_EXISTS("El usuario con el email indicado no existe"),
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
