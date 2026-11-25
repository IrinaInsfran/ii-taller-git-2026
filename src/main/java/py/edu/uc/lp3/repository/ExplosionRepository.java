package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import py.edu.uc.lp3.domain.Explosion;

/**
 * Historial de explosiones simuladas. Las consultas por nombre de método las
 * implementa Spring Data a partir del nombre.
 */
@Repository
public interface ExplosionRepository extends JpaRepository<Explosion, Long> {

	/** Todas las explosiones, de la más reciente a la más vieja. */
	List<Explosion> findAllByOrderByIdDesc();

	/** Las explosiones que provocó un creeper (normalmente una sola). */
	List<Explosion> findByCreeperId(Long creeperId);
}
