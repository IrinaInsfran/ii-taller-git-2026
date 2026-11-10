package py.edu.uc.lp3.service;

import java.util.List;

import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.domain.Entidad;

/**
 * Capa de negocio del CRUD de entidades.
 */
public interface EntidadService {

	Entidad crear(EntidadRequest datos);

	Entidad obtenerPorId(Long id);

	List<Entidad> obtenerTodas();

	Entidad actualizar(Long id, EntidadRequest datos);

	void eliminar(Long id);
}
