package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Jugada;

/** Jugadas del escenario de POO-09 guardadas en H2. */
public interface JugadaRepository extends JpaRepository<Jugada, Long> {

	/** La más reciente primero. */
	List<Jugada> findAllByOrderByIdDesc();
}
