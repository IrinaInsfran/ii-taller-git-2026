package py.edu.uc.lp3.rest.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import py.edu.uc.lp3.dto.ErrorCompaneraResponse;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.exceptions.CompaneraException;
import py.edu.uc.lp3.exceptions.CompaneraNoDisponibleException;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.HerramientaRotaException;
import py.edu.uc.lp3.exceptions.JugadaRechazadaException;
import py.edu.uc.lp3.exceptions.MinecraftException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;

/**
 * Traduce las excepciones que lanzan los servicios a respuestas HTTP: un
 * {@code @ExceptionHandler} por tipo. Spring elige el handler del tipo más
 * cercano a la excepción lanzada, así que una subclase sin handler propio
 * caería en el de su padre.
 * <ul>
 *   <li>{@link DatoInvalidoException} → 400: corregir los datos;</li>
 *   <li>{@link EntidadNoEncontradaException} → 404: revisar el id;</li>
 *   <li>{@link EntidadMuertaException} → 409: el estado actual de la
 *       entidad (muerta) choca con lo que se pide;</li>
 *   <li>{@link FueraDeAlcanceException} → 422: hace falta acercarse;</li>
 *   <li>{@link HerramientaRotaException} → 422: la herramienta tiene
 *       durabilidad 0 y hay que cambiarla;</li>
 *   <li>{@link ReglaDelJuegoException} (el resto) → 422: los datos están
 *       bien, pero el juego no permite esa acción;</li>
 *   <li>{@link ServicioExternoException} → 502: otra API (el mundo de Ceci,
 *       la API Minecraft remota, restful-api.dev) falló o no respondió;</li>
 *   <li>{@link JugadaRechazadaException} → 409: la regla de una compañera
 *       (A2 o A3) rechazó la jugada del escenario;</li>
 *   <li>{@link CompaneraNoDisponibleException} → 502: la API de una
 *       compañera no respondió.</li>
 * </ul>
 * El cuerpo siempre trae {@code error} (el tipo), {@code codigo} (el caso
 * concreto) y {@code mensaje} (el texto para una persona). Los errores de
 * una compañera agregan {@code origen}, {@code companera},
 * {@code estadoCompanera} y {@code respuestaCompanera}.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(DatoInvalidoException.class)
    public ResponseEntity<ErrorResponse> datoInvalido(
            DatoInvalidoException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", excepcion);
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> entidadNoEncontrada(
            EntidadNoEncontradaException excepcion) {

        return construir(HttpStatus.NOT_FOUND, "no_encontrada", excepcion);
    }

    @ExceptionHandler(EntidadMuertaException.class)
    public ResponseEntity<ErrorResponse> entidadMuerta(
            EntidadMuertaException excepcion) {

        return construir(HttpStatus.CONFLICT, "entidad_muerta", excepcion);
    }

    @ExceptionHandler(FueraDeAlcanceException.class)
    public ResponseEntity<ErrorResponse> fueraDeAlcance(
            FueraDeAlcanceException excepcion) {

        return construir(HttpStatus.UNPROCESSABLE_CONTENT, "fuera_de_alcance", excepcion);
    }

    @ExceptionHandler(HerramientaRotaException.class)
    public ResponseEntity<ErrorResponse> herramientaRota(
            HerramientaRotaException excepcion) {

        return construir(HttpStatus.UNPROCESSABLE_CONTENT, "herramienta_rota", excepcion);
    }

    @ExceptionHandler(ReglaDelJuegoException.class)
    public ResponseEntity<ErrorResponse> reglaDelJuego(
            ReglaDelJuegoException excepcion) {

        return construir(HttpStatus.UNPROCESSABLE_CONTENT, "regla_del_juego", excepcion);
    }

    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<ErrorResponse> servicioExterno(
            ServicioExternoException excepcion) {

        return construir(HttpStatus.BAD_GATEWAY, "servicio_externo", excepcion);
    }

    @ExceptionHandler(JugadaRechazadaException.class)
    public ResponseEntity<ErrorCompaneraResponse> jugadaRechazada(
            JugadaRechazadaException excepcion) {

        return construir(HttpStatus.CONFLICT, "jugada_rechazada", excepcion);
    }

    @ExceptionHandler(CompaneraNoDisponibleException.class)
    public ResponseEntity<ErrorCompaneraResponse> companeraNoDisponible(
            CompaneraNoDisponibleException excepcion) {

        return construir(HttpStatus.BAD_GATEWAY, "companera_no_disponible", excepcion);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(
            HttpMessageNotReadableException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", "json_invalido",
                "El cuerpo no es un JSON válido o tiene un valor de tipo incorrecto "
                + "(tipo admite: JUGADOR, ZOMBIE, ESQUELETO, CREEPER, ALDEANO, ANIMAL).");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> parametroInvalido(
            MethodArgumentTypeMismatchException excepcion) {

        return construir(HttpStatus.BAD_REQUEST, "dato_invalido", "parametro_invalido",
                "El valor '" + excepcion.getValue() + "' no sirve para el parámetro '"
                + excepcion.getName() + "'.");
    }

    private ResponseEntity<ErrorResponse> construir(
            HttpStatus estado,
            String error,
            MinecraftException excepcion) {

        return construir(estado, error, excepcion.getCodigo(), excepcion.getMessage());
    }

    private ResponseEntity<ErrorResponse> construir(
            HttpStatus estado,
            String error,
            String codigo,
            String mensaje) {

        // Un record sale en JSON con sus campos en el orden declarado: error, código, mensaje.
        ErrorResponse cuerpo = new ErrorResponse(error, codigo, mensaje == null ? "" : mensaje);

        return ResponseEntity.status(estado).body(cuerpo);
    }

    private ResponseEntity<ErrorCompaneraResponse> construir(
            HttpStatus estado,
            String error,
            CompaneraException excepcion) {

        return ResponseEntity.status(estado).body(new ErrorCompaneraResponse(
                error,
                excepcion.getCodigo(),
                excepcion.getOrigen(),
                excepcion.getCompanera(),
                excepcion.getEstadoCompanera(),
                excepcion.getRespuestaCompanera(),
                excepcion.getMessage()));
    }
}
