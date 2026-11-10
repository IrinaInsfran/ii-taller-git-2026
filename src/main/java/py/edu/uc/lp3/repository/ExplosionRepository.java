package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import py.edu.uc.lp3.domain.Explosion;

/**
 * Historial de explosiones simuladas. Las consultas por nombre de metodo las
 * implementa Spring Data a partir del nombre.
 */
@Repository
public interface ExplosionRepository extends JpaRepository<Explosion, Long> {

	/** Todas las explosiones, de la mas reciente a la mas vieja. */
	List<Explosion> findAllByOrderByIdDesc();

	/** Las explosiones que provoco un creeper (normalmente una sola). */
	List<Explosion> findByCreeperId(Long creeperId);
}
