package py.edu.uc.lp3.constants;

/**
 * Centraliza las rutas de la API REST para que controllers, tests y README
 * hablen de las mismas URLs.
 */
public final class ApiPaths {

	private static final String BASE_API = "/api/minecraft";

	public static final String INDEX = "/";

	/** Recurso del CRUD: la familia Entidad. */
	public static final String ENTIDADES = BASE_API + "/entidades";

	/** Protocolo Amenaza: quién puede dañar a la entidad {id}. */
	public static final String AMENAZAS = ENTIDADES + "/{id}/amenazas";

	/** Comportamiento polimórfico de las entidades guardadas. */
	public static final String COMPORTAMIENTOS = BASE_API + "/comportamientos";

	/** Simulación: crear una explosión la detona; el historial queda guardado. */
	public static final String EXPLOSIONES = BASE_API + "/explosiones";

	private ApiPaths() {
	}
}
