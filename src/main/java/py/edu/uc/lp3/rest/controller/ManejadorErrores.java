package py.edu.uc.lp3.rest.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.MinecraftException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Traduce las excepciones del dominio a respuestas HTTP: un
 * {@code @ExceptionHandler} por tipo. Spring elige el handler del tipo mas
 * cercano a la excepcion lanzada, asi que una subclase sin handler propio
 * (por ejemplo HerramientaRotaException) cae en el de su padre.
 * <ul>
 *   <li>{@link DatoInvalidoException} → 400: corregir los datos;</li>
 *   <li>{@link EntidadNoEncontradaException} → 404: revisar el id;</li>
 *   <li>{@link EntidadMuertaException} → 409: el estado actual de la
 *       entidad (muerta) choca con lo que se pide;</li>
 *   <li>{@link FueraDeAlcanceException} → 422: hace falta acercarse;</li>
 *   <li>{@link ReglaDelJuegoException} (el resto) → 422: los datos estan
 *       bien, pero el juego no permite esa accion.</li>
 * </ul>
 * El cuerpo siempre trae {@code error} (el tipo), {@code codigo} (el caso
 * concreto) y {@code mensaje} (el texto para una persona).
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<Map<String, String>> datoInvalido(
            DatoInvalidoException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", excepcion);
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> entidadNoEncontrada(
            EntidadNoEncontradaException excepcion) {

        return construir(HttpStatus.NOT_FOUND, "no_encontrada", excepcion);
    }

    @ExceptionHandler(EntidadMuertaException.class)
    public ResponseEntity<Map<String, String>> entidadMuerta(
            EntidadMuertaException excepcion) {

        return construir(HttpStatus.CONFLICT, "entidad_muerta", excepcion);
    }

    @ExceptionHandler(FueraDeAlcanceException.class)
    public ResponseEntity<Map<String, String>> fueraDeAlcance(
            FueraDeAlcanceException excepcion) {

        return construir(HttpStatus.UNPROCESSABLE_CONTENT, "fuera_de_alcance", excepcion);
    }

    @ExceptionHandler(ReglaDelJuegoException.class)
    public ResponseEntity<Map<String, String>> reglaDelJuego(
            ReglaDelJuegoException excepcion) {

        return construir(HttpStatus.UNPROCESSABLE_CONTENT, "regla_del_juego", excepcion);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> cuerpoIlegible(
            HttpMessageNotReadableException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", "json_invalido",
                "El cuerpo no es un JSON válido o tiene un valor de tipo incorrecto "
                + "(tipo admite: JUGADOR, ZOMBIE, ESQUELETO, CREEPER, ALDEANO, ANIMAL).");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(
            MethodArgumentTypeMismatchException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", "parametro_invalido",
                "El valor '" + excepcion.getValue() + "' no sirve para el parámetro '"
                + excepcion.getName() + "'.");
    }

    private ResponseEntity<Map<String, String>> construir(
            HttpStatus estado,
            String error,
            MinecraftException excepcion) {

        return construir(estado, error, excepcion.getCodigo(), excepcion.getMessage());
    }

    private ResponseEntity<Map<String, String>> construir(
            HttpStatus estado,
            String error,
            String codigo,
            String mensaje) {

        // LinkedHashMap para que las claves salgan siempre en el mismo orden
        Map<String, String> cuerpo = new LinkedHashMap<>();
        cuerpo.put("error", error);
        cuerpo.put("codigo", codigo);
        cuerpo.put("mensaje", mensaje == null ? "" : mensaje);

        return ResponseEntity.status(estado).body(cuerpo);
    }

}
