package py.edu.uc.lp3.web;

// Clase estática que centraliza todas las constantes
// que utilizaremos como parte de la API REST
public class ApiPaths {

	private static final String BASE_API = "/api/minecraft";

	public static final String INDEX = "/";

	// Operaciones con entidades del juego
	public static final String CREEPER = BASE_API + "/creeper";
	public static final String COMPORTAMIENTOS = BASE_API + "/comportamientos";

}