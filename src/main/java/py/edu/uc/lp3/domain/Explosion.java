package py.edu.uc.lp3.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Explosion en el mundo: daña a las entidades segun su distancia al centro.
 * <p>
 * Regla 2, "onda expansiva". Usa la formula de Minecraft Java para una
 * victima sin bloques que la cubran:
 * <ul>
 *   <li>el radio de daño es el doble de la potencia;</li>
 *   <li>{@code impacto = 1 - distancia / radio};</li>
 *   <li>{@code daño = (impacto² + impacto) / 2 * 7 * radio + 1}.</li>
 * </ul>
 * Para un creeper (potencia 3) el radio es 6 bloques y el daño en el centro
 * es 43: mata a un jugador sin armadura que este muy cerca.
 * <p>
 * Se persiste como registro de la simulacion: despues de detonar guarda
 * quien la provoco y cuantas entidades alcanzo y mato. Una explosion
 * detona una sola vez.
 */
@Entity
public class Explosion {

    /** Lo que la explosion le hizo a una entidad alcanzada. */
    public record Impacto(Entidad entidad, double distancia, int dano) {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double centroX;
    private double centroY;
    private double centroZ;
    private double potencia;

    /** Ids y no relaciones: borrar al creeper o al jugador no borra la historia. */
    private Long creeperId;
    private Long jugadorObjetivoId;
    private String jugadorObjetivo;

    private boolean detonada;
    private Instant momento;
    private int entidadesAlcanzadas;
    private int entidadesMuertas;
    private int danoTotal;

    /** Lo que paso en esta detonacion; no se guarda, se resume en los contadores. */
    @Transient
    private List<Impacto> impactos = List.of();

    /** Constructor requerido por JPA para reconstruir la explosion desde la base. */
    protected Explosion() {
    }

    public Explosion(double centroX, double centroY, double centroZ, double potencia) {

        if (!Double.isFinite(centroX)
                || !Double.isFinite(centroY)
                || !Double.isFinite(centroZ)) {
            throw new DatoInvalidoException("El centro de la explosión debe contener valores válidos.");
        }

        if (!Double.isFinite(potencia) || potencia <= 0) {
            throw new DatoInvalidoException("La potencia de la explosión debe ser mayor que cero.");
        }

        this.centroX = centroX;
        this.centroY = centroY;
        this.centroZ = centroZ;
        this.potencia = potencia;
    }

    /**
     * La explosion de un creeper: en su posicion, con su potencia, provocada
     * por {@code objetivo} (puede ser null si exploto sin jugador cerca).
     * Solo la crea {@link Creeper}.
     */
    Explosion(Creeper creeper, Jugador objetivo) {
        this(creeper.getPosicionX(),
                creeper.getPosicionY(),
                creeper.getPosicionZ(),
                Creeper.POTENCIA_EXPLOSION);

        this.creeperId = creeper.getId();

        if (objetivo != null) {
            this.jugadorObjetivoId = objetivo.getId();
            this.jugadorObjetivo = objetivo.getNombre();
        }
    }

    public double getRadio() {
        return potencia * 2;
    }

    public double distanciaA(Entidad entidad) {
        double dx = entidad.getPosicionX() - centroX;
        double dy = entidad.getPosicionY() - centroY;
        double dz = entidad.getPosicionZ() - centroZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Daño que recibe una entidad a esa distancia del centro; 0 si esta
     * fuera del radio.
     */
    public int calcularDano(double distancia) {

        if (!Double.isFinite(distancia) || distancia < 0) {
            throw new DatoInvalidoException("La distancia debe ser un valor válido y no negativo.");
        }

        double radio = getRadio();

        if (distancia >= radio) {
            return 0;
        }

        double impacto = 1 - distancia / radio;

        return (int) ((impacto * impacto + impacto) / 2 * 7 * radio + 1);
    }

    /**
     * Aplica el daño a cada entidad viva dentro del radio y devuelve a
     * quienes alcanzo. Las entidades muertas o fuera del radio no cambian.
     *
     * @throws ReglaDelJuegoException si esta explosion ya detono
     */
    public List<Impacto> detonar(List<? extends Entidad> entidades) {

        if (detonada) {
            throw new ReglaDelJuegoException(
                    "explosion_ya_detonada",
                    "Una explosión detona una sola vez.");
        }

        if (entidades == null) {
            throw new DatoInvalidoException("La lista de entidades no puede ser nula.");
        }

        List<Impacto> alcanzadas = new ArrayList<>();

        for (Entidad entidad : entidades) {

            if (entidad == null || !entidad.estaViva()) {
                continue;
            }

            double distancia = distanciaA(entidad);
            int dano = calcularDano(distancia);

            if (dano > 0) {
                entidad.recibirDano(dano);
                alcanzadas.add(new Impacto(entidad, distancia, dano));
            }
        }

        detonada = true;
        momento = Instant.now();
        impactos = List.copyOf(alcanzadas);
        entidadesAlcanzadas = alcanzadas.size();
        entidadesMuertas = (int) alcanzadas.stream()
                .filter(impacto -> !impacto.entidad().estaViva())
                .count();
        danoTotal = alcanzadas.stream().mapToInt(Impacto::dano).sum();

        return impactos;
    }

    public Long getId() {
        return id;
    }

    public double getPotencia() {
        return potencia;
    }

    public double getCentroX() {
        return centroX;
    }

    public double getCentroY() {
        return centroY;
    }

    public double getCentroZ() {
        return centroZ;
    }

    public Long getCreeperId() {
        return creeperId;
    }

    public Long getJugadorObjetivoId() {
        return jugadorObjetivoId;
    }

    public String getJugadorObjetivo() {
        return jugadorObjetivo;
    }

    public boolean isDetonada() {
        return detonada;
    }

    public Instant getMomento() {
        return momento;
    }

    public int getEntidadesAlcanzadas() {
        return entidadesAlcanzadas;
    }

    public int getEntidadesMuertas() {
        return entidadesMuertas;
    }

    public int getDanoTotal() {
        return danoTotal;
    }

    /** Impactos de la detonacion recien hecha; vacio si se leyo de la base. */
    public List<Impacto> getImpactos() {
        return impactos;
    }
}
