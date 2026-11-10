package py.edu.uc.lp3.domain;

import java.util.Comparator;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;

/**
 * Creeper: entidad hostil que explota tras un tiempo de detonación.
 * <p>
 * Participa en las dos reglas de la simulacion:
 * <ol>
 *   <li><b>Mecha corta</b> ({@link #elegirObjetivo(List)}): solo enciende la
 *       mecha si hay un jugador vivo a {@value #DISTANCIA_DE_DETONACION}
 *       bloques o menos, y apunta al mas cercano;</li>
 *   <li><b>Onda expansiva</b> ({@link Explosion#detonar(List)}): al explotar
 *       muere, y la explosion daña a cada entidad viva de su radio, mas cuanto
 *       mas cerca este.</li>
 * </ol>
 */
@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends EntidadHostil {

    /** Valores por defecto de un creeper "normal" del juego. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 1;
    public static final double RANGO_DETECCION_POR_DEFECTO = 16;
    public static final int DANO_ATAQUE_POR_DEFECTO = 3;
    public static final int TIEMPO_EXPLOSION_POR_DEFECTO = 30;

    /** Potencia de la explosion de un creeper normal en Minecraft. */
    public static final double POTENCIA_EXPLOSION = 3;

    /** El creeper solo empieza a detonar si su objetivo esta a 3 bloques o menos. */
    public static final double DISTANCIA_DE_DETONACION = 3;

    private int tiempoExplosion;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Creeper() {
    }

    /**
     * Constructor sobrecargado: crea un creeper con los valores por defecto
     * del juego y solo recibe su posicion. Delega en el constructor
     * completo con this(...) para no duplicar la validacion.
     */
    public Creeper(double posicionX, double posicionY, double posicionZ) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                RANGO_DETECCION_POR_DEFECTO,
                DANO_ATAQUE_POR_DEFECTO,
                TIEMPO_EXPLOSION_POR_DEFECTO
        );
    }

    public Creeper(int salud,
                   double posicionX,
                   double posicionY,
                   double posicionZ,
                   int velocidad,
                   double rangoDeteccion,
                   int danoAtaque,
                   int tiempoExplosion) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                rangoDeteccion,
                danoAtaque
        );

        if (tiempoExplosion <= 0) {
            throw new DatoInvalidoException(
                    "El tiempo de explosión debe ser mayor que cero."
            );
        }

        this.tiempoExplosion = tiempoExplosion;
    }

    @Override
    public void atacar(Entidad objetivo) {
        super.atacar(objetivo);
        explotar();
    }

    /**
     * Indica si el jugador esta lo bastante cerca para que el creeper
     * empiece a detonar.
     */
    public boolean puedeDetonarCerca(Jugador jugador) {

        if (jugador == null) {
            throw new DatoInvalidoException("El jugador no puede ser nulo.");
        }

        return estaViva()
                && jugador.estaViva()
                && distanciaA(jugador) <= DISTANCIA_DE_DETONACION;
    }

    /**
     * Regla 1, "mecha corta": de los jugadores dados, elige al jugador vivo
     * mas cercano que este a {@value #DISTANCIA_DE_DETONACION} bloques o
     * menos.
     *
     * @throws EntidadMuertaException  si el creeper ya exploto
     * @throws FueraDeAlcanceException si ningun jugador esta tan cerca
     */
    public Jugador elegirObjetivo(List<Jugador> jugadores) {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un Creeper muerto no puede explotar."
            );
        }

        if (jugadores == null) {
            throw new DatoInvalidoException(
                    "La lista de jugadores no puede ser nula."
            );
        }

        return jugadores.stream()
                .filter(this::puedeDetonarCerca)
                .min(Comparator.comparingDouble(this::distanciaA))
                .orElseThrow(() -> new FueraDeAlcanceException(
                        "El creeper solo detona con un jugador vivo a "
                        + (int) DISTANCIA_DE_DETONACION
                        + " bloques o menos, y no hay ninguno."));
    }

    /**
     * La simulacion completa: aplica la regla 1 para elegir a que jugador
     * reacciona y, si lo encuentra, explota sobre {@code alrededor} (regla 2).
     * Si la regla 1 falla no cambia nada: ni el creeper ni las entidades.
     *
     * @return la explosion ya detonada, con el resumen de lo que hizo
     */
    public Explosion explotarCercaDe(List<Jugador> jugadores,
                                     List<? extends Entidad> alrededor) {

        Jugador objetivo = elegirObjetivo(jugadores);

        if (alrededor == null) {
            throw new DatoInvalidoException(
                    "La lista de entidades alrededor no puede ser nula."
            );
        }

        Explosion explosion = new Explosion(this, objetivo);
        morir();
        explosion.detonar(alrededor);
        return explosion;
    }

    public void explotar() {
        explotar(List.of());
    }

    /**
     * Explota en su posicion, sin buscar jugador: el creeper muere y la
     * explosion daña a las entidades de {@code alrededor} segun su distancia.
     * Si el propio creeper esta en la lista no se daña, porque ya murio.
     */
    public List<Explosion.Impacto> explotar(List<? extends Entidad> alrededor) {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un Creeper muerto no puede explotar."
            );
        }

        if (alrededor == null) {
            throw new DatoInvalidoException(
                    "La lista de entidades alrededor no puede ser nula."
            );
        }

        Explosion explosion = new Explosion(this, null);
        morir();
        return explosion.detonar(alrededor);
    }

    /**
     * La amenaza de un creeper no es su golpe sino su explosion: si el
     * objetivo esta dentro de su rango de deteccion, el creeper se acerca y
     * le hace el daño de la explosion a la distancia actual.
     */
    @Override
    public int danoContra(Entidad objetivo) {

        if (super.danoContra(objetivo) == 0) {
            return 0;
        }

        Explosion posible = new Explosion(
                getPosicionX(), getPosicionY(), getPosicionZ(), POTENCIA_EXPLOSION);

        return Math.max(getDanoAtaque(), posible.calcularDano(distanciaA(objetivo)));
    }

    @Override
    public String describirAmenaza() {
        return "Creeper: explota con potencia " + POTENCIA_EXPLOSION
                + " y daña todo lo que esté a menos de "
                + POTENCIA_EXPLOSION * 2 + " bloques.";
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        tiempoExplosion = ((Creeper) otra).tiempoExplosion;
    }

    public int getTiempoExplosion() {
        return tiempoExplosion;
    }

    @Override
    public String describirComportamiento() {
        return "Se aproxima a su objetivo y explota después de su tiempo de detonación.";
    }
}
