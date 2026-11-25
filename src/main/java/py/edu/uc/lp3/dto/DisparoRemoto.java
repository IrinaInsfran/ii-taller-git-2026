package py.edu.uc.lp3.dto;

/**
 * Respuesta de {@code GET /esqueleto/disparar} de la API Minecraft remota.
 * <p>
 * Esa API crea un esqueleto con {@code flechasIniciales}, dispara una vez sin
 * distancia (siempre gasta una flecha) y después otra a {@code distanciaPedida}:
 * esa segunda flecha solo sale, y se gasta, si la distancia no supera su
 * {@code rango}.
 */
public record DisparoRemoto(
		String nombre,
		int flechasIniciales,
		int flechasRestantes,
		double rango,
		double distanciaPedida,
		String disparoSinDistancia,
		String disparoConDistancia) {

	/**
	 * La flecha con distancia salió: después del primer disparo quedaban
	 * {@code flechasIniciales - 1}; si quedan menos, se gastó otra.
	 */
	public boolean alcanzo() {
		return flechasRestantes < flechasIniciales - 1;
	}
}
