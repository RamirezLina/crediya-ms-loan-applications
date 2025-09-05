package co.com.crediya.model.error;

public class AppException extends RuntimeException {

    public enum Type{
        MS_REQUEST_400_ERROR("La peticion al microservicio de autenticacion retorno un error 400: %s "),
        MS_REQUEST_500_ERROR("La peticion al microservicio de autenticacion retorno un error 500: %s ");
        
        private final String message;

        public AppException build(){
            return new AppException(this);
        }

        public AppException build(String personalizedMessage){
            String finalMessage = String.format(this.message, personalizedMessage);
            return new AppException(finalMessage);
        }

        Type(String message) {
            this.message = message;
        }
    }


    private AppException(Type type){
        super(type.message);
    }

    public AppException(String personalizedMessage){
        super(personalizedMessage);
    }

}
