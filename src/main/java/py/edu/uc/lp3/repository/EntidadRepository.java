package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadHostil;
import py.edu.uc.lp3.domain.Jugador;

/**
 * Persistencia de toda la familia {@link Entidad} (una sola tabla).
 * <p>
 * Es una interfaz: aca solo se declaran los mensajes; Spring Data genera la
 * implementacion. Las consultas extra salen del juego: jugadores con nombre
 * repetido, jugadores vivos (los que pueden encender a un creeper) y mobs
 * hostiles vivos (las amenazas del mundo).
 */
@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long> {

	/**
	 * Indica si ya hay otro jugador con ese nombre (sin distinguir
	 * mayusculas). {@code idExcluido} permite ignorar al propio jugador
	 * cuando se lo esta actualizando; en una alta se pasa null.
	 */
	@Query("""
			select count(j) > 0 from Jugador j
			where lower(j.nombre) = lower(:nombre)
			  and (:idExcluido is null or j.id <> :idExcluido)
			""")
	boolean existeJugadorConNombre(@Param("nombre") String nombre,
	                               @Param("idExcluido") Long idExcluido);

	@Query("select j from Jugador j where j.salud > 0")
	List<Jugador> buscarJugadoresVivos();

	@Query("select h from EntidadHostil h where h.salud > 0 order by h.id")
	List<EntidadHostil> buscarHostilesVivos();
}
