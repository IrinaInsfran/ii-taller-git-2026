package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadHostil;
import py.edu.uc.lp3.domain.EntidadPasiva;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;

/**
 * Las acciones del juego con sus reglas: cada método valida, lanza la
 * excepción que corresponde y, si se puede, le pide a la entidad que cambie.
 * No usa la base: quien lo llama decide si guarda.
 */
public interface AccionService {

	/** Sobrecarga: se mueve en el plano horizontal, sin cambiar la altura. */
	void mover(Entidad entidad, double dx, double dz);

	/** Se mueve (dx, dy, dz); el desplazamiento no puede superar su velocidad. */
	void mover(Entidad entidad, double dx, double dy, double dz);

	void recibirDano(Entidad entidad, int cantidad);

	void curar(Entidad entidad, int cantidad);

	/** Un mob hostil ataca a un objetivo dentro de su rango de detección. */
	void atacar(EntidadHostil atacante, Entidad objetivo);

	/** Un jugador ataca con un arma a un objetivo dentro de su alcance. */
	void atacar(Jugador jugador, Entidad objetivo, Arma arma);

	/** La entidad pasiva se aleja de la amenaza tanto como su velocidad. */
	void huir(EntidadPasiva pasiva, Entidad amenaza);

	void infectar(Zombie zombie, Aldeano aldeano);

	/** @return lo que ofrece el aldeano */
	String comerciar(Aldeano aldeano);

	/** @return la descripción de la acción */
	String montar(Animal animal, Jugador jugador);

	void ganarExperiencia(Jugador jugador, int cantidad);
}
