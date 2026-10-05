package py.edu.uc.lp3.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del dominio a respuestas HTTP.
 * <p>
 * El controller no valida ni corrige valores: las reglas viven en las clases
 * del dominio y este handler solo las hace observables en la API.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> errorDeArgumento(
            IllegalArgumentException excepcion) {

        return construir("argumento_invalido", excepcion.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> errorDeEstado(
            IllegalStateException excepcion) {

        return construir("estado_invalido", excepcion.getMessage());
    }

    private ResponseEntity<Map<String, String>> construir(
            String error,
            String mensaje) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", error,
                        "mensaje", mensaje == null ? "" : mensaje
                ));
    }

}
