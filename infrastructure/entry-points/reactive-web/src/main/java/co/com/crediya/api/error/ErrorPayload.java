package co.com.crediya.api.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorPayload", description = "Estructura de error estándar")
public class ErrorPayload {

    @Schema(example = "Path de la peticion")
    public String path;

    @Schema(example = "Mensaje de error personalizado o de la excepcion lanzada")
    public String errorMessage;

    @Schema(example = "Tipo de excepcion")
    public String exceptionClass;

    @Schema(example = "Clase desde donde se lanzo la excepcion")
    public String generatedInClass;

    @Schema(example = "Metodo de la clase en el que se genero la excepcion")
    public String generatedInMethod;

    @Schema(example = "Linea de la clase en el que se genero la excepcion")
    public Integer generatedInLine;

    @Schema(name = "initCause",
            example = "Causa inicial de la excepcion. Si es una excepcion personalizada será una validacion de logica de negocio")
    public String initCause;

}
