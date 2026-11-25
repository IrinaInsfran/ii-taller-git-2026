package py.edu.uc.lp3.domain;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * Explosión en el mundo, guardada como registro de la simulación.
 * <p>
 * Solo guarda el estado: dónde fue, con qué potencia, quién la provocó y el
 * resumen de lo que hizo (entidades alcanzadas, muertas y daño total). La
 * regla 2, "onda expansiva" (a quién daña y cuánto), la aplica
 * {@code ReglasCreeperServiceImpl}, que al terminar le pasa los impactos a
 * {@link #registrarDetonacion(List)}.
 */
@Entity
public class Explosion {

    /** Lo que la explosión le hizo a una entidad alcanzada. */
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

    /** Lo que pasó en esta detonación; no se guarda, se resume en los contadores. */
    @Transient
    private List<Impacto> impactos = List.of();

    /** Constructor requerido por JPA para reconstruir la explosión desde la base. */
    protected Explosion() {
    }

    /** Una explosión suelta, sin creeper ni jugador asociados. */
    public Explosion(double centroX, double centroY, double centroZ, double potencia) {
        this.centroX = centroX;
        this.centroY = centroY;
        this.centroZ = centroZ;
        this.potencia = potencia;
    }

    /**
     * La explosión de un creeper: en su posición, con su potencia, provocada
     * por {@code objetivo} (puede ser null si explotó sin jugador cerca).
     */
    public Explosion(Creeper creeper, Jugador objetivo) {
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

    /** El radio de la explosión es el doble de su potencia. */
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
     * Guarda el resultado de la detonación: la marca como detonada, anota el
     * momento y resume los impactos en los contadores que van a H2.
     */
    public void registrarDetonacion(List<Impacto> alcanzadas) {
        detonada = true;
        momento = Instant.now();
        impactos = List.copyOf(alcanzadas);
        entidadesAlcanzadas = impactos.size();
        entidadesMuertas = (int) impactos.stream()
                .filter(impacto -> !impacto.entidad().estaViva())
                .count();
        danoTotal = impactos.stream().mapToInt(Impacto::dano).sum();
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

    /** Impactos de la detonación recién hecha; vacío si se leyó de la base. */
    public List<Impacto> getImpactos() {
        return impactos;
    }
}
