package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.dto.CompaneraResponse;
import py.edu.uc.lp3.dto.ErrorCompaneraResponse;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.JugadaRequest;
import py.edu.uc.lp3.dto.JugadaResponse;
import py.edu.uc.lp3.service.JugadaService;

/**
 * Escenario de POO-09 «el creeper pide permiso»: un creeper guardado
 * explota solo si Cecilia (A2) y Sol (A3) aceptan la jugada con sus reglas.
 */
@RestController
@Tag(name = "Jugadas (POO-09)",
		description = "Escenario: la explosión se guarda solo si A2 (Cecilia) y A3 (Sol) la aceptan")
public class JugadaController {

	private final JugadaService jugadaService;

	public JugadaController(JugadaService jugadaService) {
		this.jugadaService = jugadaService;
	}

	@PostMapping(ApiPaths.JUGADA_EXPLOSION)
	@Operation(summary = "Explosión validada por A2 y A3",
			description = "Lee el creeper y las entidades vivas a 6 bloques o menos, y le pregunta a Cecilia (A2: "
					+ "el creeper está vivo y no detonó) y después a Sol (A3: pasó el tiempo de mecha y los "
					+ "objetivos están en el radio). Solo si las dos aceptan aplica mis reglas (mecha corta y "
					+ "onda expansiva) y guarda la explosión, el daño y la jugada. Si alguna rechaza o no "
					+ "responde, H2 queda igual y la respuesta dice quién fue.")
	@ApiResponse(responseCode = "201", description = "A2 y A3 aceptaron: jugada guardada; Location apunta a ella")
	@ApiResponse(responseCode = "400", description = "Falta creeperId o tiempoTranscurrido, o es negativo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "No existe el creeper",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Rechazo de A2 o A3 (campo origen), o mi regla: el creeper ya está muerto",
			content = @Content(schema = @Schema(implementation = ErrorCompaneraResponse.class)))
	@ApiResponse(responseCode = "422", description = "Mi regla: nadie en el radio o ningún jugador vivo a 3 bloques o menos",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "502", description = "A2 o A3 no respondió (campo origen); no se guardó nada",
			content = @Content(schema = @Schema(implementation = ErrorCompaneraResponse.class)))
	public ResponseEntity<JugadaResponse> explosion(@RequestBody JugadaRequest pedido) {
		JugadaResponse jugada = jugadaService.registrarExplosion(pedido);
		return ResponseEntity.created(URI.create(ApiPaths.JUGADAS + "/" + jugada.id())).body(jugada);
	}

	@GetMapping(ApiPaths.JUGADAS)
	@Operation(summary = "Jugadas guardadas", description = "La más reciente primero")
	@ApiResponse(responseCode = "200", description = "Lista (puede estar vacía)")
	public List<JugadaResponse> listar() {
		return jugadaService.listar();
	}

	@GetMapping(ApiPaths.JUGADAS + "/{id}")
	@Operation(summary = "Obtener una jugada por id")
	@ApiResponse(responseCode = "200", description = "La jugada")
	@ApiResponse(responseCode = "404", description = "No existe una jugada con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public JugadaResponse obtener(@Parameter(description = "Id de la jugada", example = "1")
	                              @PathVariable Long id) {
		return jugadaService.obtener(id);
	}

	@GetMapping(ApiPaths.JUGADAS_COMPANERAS)
	@Operation(summary = "A quién consulta el escenario",
			description = "Rol, nombre y URL de cada compañera, según la configuración actual")
	@ApiResponse(responseCode = "200", description = "A2 y A3")
	public List<CompaneraResponse> companeras() {
		return jugadaService.companeras();
	}
}
