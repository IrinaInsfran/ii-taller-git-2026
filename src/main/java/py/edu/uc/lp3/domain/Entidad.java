package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Clase base que representa cualquier entidad del mundo del juego.
 * <p>
 * Solo guarda el estado encapsulado: no hay setters públicos y cada cambio
 * pasa por un mensaje con nombre del juego ({@link #mover}, {@link #recibirDano},
 * {@link #curar}...). Las validaciones, las reglas y las excepciones están
 * en la capa {@code service}, que decide si el cambio se puede hacer antes de
 * pedírselo a la entidad. Los métodos de esta clase no lanzan excepciones:
 * mantienen la salud entre 0 y la salud máxima sin importar qué reciban.
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
        this.salud = salud;
        this.saludMaxima = salud;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.posicionZ = posicionZ;
        this.velocidad = velocidad;
    }

    /**
     * Desplaza la entidad en el plano horizontal (dx, dz), sin cambiar su
     * altura. Es una sobrecarga útil porque moverse en Minecraft casi nunca
     * cambia la coordenada Y.
     */
    public void mover(double dx, double dz) {
        mover(dx, 0, dz);
    }

    /** Desplaza la entidad una cantidad (dx, dy, dz). */
    public void mover(double dx, double dy, double dz) {
        posicionX += dx;
        posicionY += dy;
        posicionZ += dz;
    }

    public double distanciaA(Entidad otra) {
        double dx = otra.posicionX - posicionX;
        double dy = otra.posicionY - posicionY;
        double dz = otra.posicionZ - posicionZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Resta salud. La cuenta se hace en {@code long} y se acota entre 0 y la
     * salud máxima, así que la salud nunca queda negativa ni desborda.
     */
    public void recibirDano(int cantidad) {
        salud = acotar((long) salud - cantidad);
    }

    /** Suma salud sin pasar de la salud máxima (la cuenta tampoco desborda). */
    public void curar(int cantidad) {
        salud = acotar((long) salud + cantidad);
    }

    /** La entidad muere: su salud queda en 0. */
    public void morir() {
        salud = 0;
    }

    public boolean estaViva() {
        return salud > 0;
    }

    private int acotar(long valor) {
        return (int) Math.max(0, Math.min(saludMaxima, valor));
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
     * (lo usa el PUT). No hay un setter por campo y el id de esta entidad no
     * cambia. Que {@code nueva} sea del mismo tipo y que esta entidad siga
     * viva lo verifica el servicio antes de llamar.
     */
    public final void reemplazarCon(Entidad nueva) {
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
