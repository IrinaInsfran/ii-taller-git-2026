package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Clase base que representa cualquier entidad del mundo del juego.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_entidad")
public abstract class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int salud;
    private int saludMaxima;
    private double posicionX;
    private double posicionY;
    private double posicionZ;
    private int velocidad;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Entidad() {
    }

    public Entidad(int salud, double posicionX, double posicionY,
                   double posicionZ, int velocidad) {

        if (salud <= 0) {
            throw new DatoInvalidoException("La salud debe ser mayor que cero.");
        }

        if (velocidad < 0) {
            throw new DatoInvalidoException("La velocidad no puede ser negativa.");
        }

        if (!Double.isFinite(posicionX)
                || !Double.isFinite(posicionY)
                || !Double.isFinite(posicionZ)) {
            throw new DatoInvalidoException("La posición debe contener valores válidos.");
        }

        this.salud = salud;
        this.saludMaxima = salud;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.posicionZ = posicionZ;
        this.velocidad = velocidad;
    }

    /**
     * Desplaza la entidad en el plano horizontal (dx, dz), sin cambiar su
     * altura. Es una sobrecarga util porque moverse en Minecraft casi nunca
     * cambia la coordenada Y.
     */
    public void mover(double dx, double dz) {
        mover(dx, 0, dz);
    }

    /**
     * Desplaza la entidad una cantidad (dx, dy, dz).
     * La magnitud del desplazamiento no puede superar su velocidad.
     */
    public void mover(double dx, double dy, double dz) {

        if (!estaViva()) {
            throw new EntidadMuertaException("Una entidad muerta no puede moverse.");
        }

        if (!Double.isFinite(dx) || !Double.isFinite(dy) || !Double.isFinite(dz)) {
            throw new DatoInvalidoException("El desplazamiento debe contener valores válidos.");
        }

        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distancia > velocidad + 1e-9) {
            throw new ReglaDelJuegoException(
                    "excede_velocidad",
                    "La entidad no puede moverse más que su velocidad."
            );
        }

        posicionX += dx;
        posicionY += dy;
        posicionZ += dz;
    }

    public double distanciaA(Entidad otra) {
        if (otra == null) {
            throw new DatoInvalidoException("La otra entidad no puede ser nula.");
        }

        double dx = otra.posicionX - posicionX;
        double dy = otra.posicionY - posicionY;
        double dz = otra.posicionZ - posicionZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void recibirDano(int cantidad) {

        if (cantidad <= 0) {
            throw new DatoInvalidoException(
                    "La cantidad de daño debe ser mayor que cero."
            );
        }

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Una entidad muerta no puede recibir más daño."
            );
        }

        salud = Math.max(0, salud - cantidad);

        if (salud == 0) {
            morir();
        }
    }

    public void curar(int cantidad) {

        if (cantidad <= 0) {
            throw new DatoInvalidoException(
                    "La cantidad de curación debe ser mayor que cero."
            );
        }

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Una entidad muerta no puede ser curada."
            );
        }

        // Comparar contra el faltante evita sumar: salud + cantidad puede
        // desbordar int y dejar la salud en negativo, violando el
        // invariante 0 <= salud <= saludMaxima.
        int faltante = saludMaxima - salud;

        salud = (cantidad >= faltante)
                ? saludMaxima
                : salud + cantidad;
    }

    protected void morir() {
        salud = 0;
        System.out.println("La entidad ha muerto.");
    }

    public boolean estaViva() {
        return salud > 0;
    }

    public int getSalud() {
        return salud;
    }

    public int getSaludMaxima() {
        return saludMaxima;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public double getPosicionY() {
        return posicionY;
    }

    public double getPosicionZ() {
        return posicionZ;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public Long getId() {
        return id;
    }

    /**
     * Reemplaza todo el estado de esta entidad por el de {@code nueva}
     * (lo usa el PUT). No hay un setter por campo: {@code nueva} ya paso por
     * un constructor, asi que sus valores cumplen los invariantes, y el id
     * de esta entidad no cambia.
     * <p>
     * Reglas: no se puede cambiar de tipo (un zombie no pasa a ser jugador)
     * y una entidad muerta no se edita, porque un PUT no es una forma de
     * resucitar.
     */
    public final void reemplazarCon(Entidad nueva) {

        if (nueva == null) {
            throw new DatoInvalidoException("La entidad nueva no puede ser nula.");
        }

        if (nueva.getClass() != getClass()) {
            throw new DatoInvalidoException(
                    "cambio_de_tipo",
                    "No se puede cambiar el tipo de una entidad: la entidad "
                    + id + " es " + getClass().getSimpleName().toUpperCase() + ".");
        }

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "La entidad " + id + " está muerta: no se puede modificar.");
        }

        copiarEstadoDe(nueva);
    }

    /**
     * Copia los campos de esta clase desde {@code otra}, que es de la misma
     * clase concreta. Cada clase hija lo sobreescribe, llama a
     * {@code super.copiarEstadoDe(otra)} y copia sus propios campos.
     */
    protected void copiarEstadoDe(Entidad otra) {
        salud = otra.salud;
        saludMaxima = otra.saludMaxima;
        posicionX = otra.posicionX;
        posicionY = otra.posicionY;
        posicionZ = otra.posicionZ;
        velocidad = otra.velocidad;
    }

    public abstract String describirComportamiento();
}
