package py.edu.uc.lp3.service;

import java.util.List;

import py.edu.uc.lp3.dto.ObjetoExterno;

/**
 * Consulta los objetos de la API pública https://api.restful-api.dev/objects.
 */
public interface ObjetoExternoService {

	/**
	 * Todos los objetos, o solo los de {@code ids} si la lista no está vacía.
	 */
	List<ObjetoExterno> listar(List<String> ids);

	/**
	 * Un objeto por id.
	 *
	 * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException si la API
	 *         externa responde 404
	 */
	ObjetoExterno buscarPorId(String id);
}
