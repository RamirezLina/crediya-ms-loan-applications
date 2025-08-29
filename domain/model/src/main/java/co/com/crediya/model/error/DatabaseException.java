package co.com.crediya.model.error;

public class DatabaseException extends RuntimeException {

    public enum Type{
        ROL_NOT_EXISTS("El rol id ingresado no se asocia a ningun rol existente"),
        DATABASE_ERROR("Error en la base de datos");
        private final String message;

        public DatabaseException build(){
            return new DatabaseException(this);
        }

        public DatabaseException build(Throwable throwable){
            return new DatabaseException(this, throwable);
        }

        Type(String message) {
            this.message = message;
        }
    }


    private DatabaseException(Type type){
        super(type.message);
    }

    private DatabaseException(Type type, Throwable throwable){
        super(type.message, throwable);
    }

}
