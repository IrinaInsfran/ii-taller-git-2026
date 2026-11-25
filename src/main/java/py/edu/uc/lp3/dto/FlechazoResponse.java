package py.edu.uc.lp3.dto;

/**
 * Resultado de un flechazo: lo que decidió la API remota (si la flecha llega)
 * y lo que aplicó este proyecto (el daño y si el objetivo murió).
 */
public record FlechazoResponse(
		Long esqueletoId,
		Long objetivoId,
		TipoEntidad objetivoTipo,
		double distancia,
		double rangoRemoto,
		boolean impacto,
		int dano,
		int saludAntes,
		int saludDespues,
		boolean objetivoMurio,
		int flechasRestantes,
		String relatoRemoto) {
}
