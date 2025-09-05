package co.com.crediya.model.error;

public class AuthException extends RuntimeException {

    public enum Type {
        USER_ACCESS_DENIED("El email del token no coincide con el email a quien se solicita el prestamo"),
        TOKEN_NOT_FOUND("No se logro extraer la informacion del token: %s");
     
        private final String message;

        public AuthException build() {
            return new AuthException(this);
        }

        public AuthException build(String personalizedMessage) {
            String finalMessage = String.format(this.message, personalizedMessage);
            return new AuthException(finalMessage);
        }

        Type(String message) {
            this.message = message;
        }
    }

    private AuthException(Type type) {
        super(type.message);
    }

    public AuthException(String personalizedMessage) {
        super(personalizedMessage);
    }
}