package Laboral;

/**
 * Error que se lanza al introducir parámetros incorrectos
 */
public class DatosNoCorrectosException extends RuntimeException {
    public DatosNoCorrectosException(String message) {
        super(message);
    }
}
