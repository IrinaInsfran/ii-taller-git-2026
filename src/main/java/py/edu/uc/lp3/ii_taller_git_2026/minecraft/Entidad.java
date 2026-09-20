package py.edu.uc.lp3.herencia;

/**
 * Clase base que representa cualquier entidad del mundo del juego.
 */
public class Entidad {

    private int salud;
    private double posicionX;
    private double posicionY;
    private double posicionZ;
    private final int velocidad;

    public Entidad(int salud, double posicionX, double posicionY,
                   double posicionZ, int velocidad) {

        if (salud <= 0) {
            throw new IllegalArgumentException(
                    "La salud debe ser mayor que cero."
            );
        }

        if (velocidad < 0) {
            throw new IllegalArgumentException(
                    "La velocidad no puede ser negativa."
            );
        }

        if (!Double.isFinite(posicionX)
                || !Double.isFinite(posicionY)
                || !Double.isFinite(posicionZ)) {
            throw new IllegalArgumentException(
                    "La posición debe contener valores válidos."
            );
        }

        this.salud = salud;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.posicionZ = posicionZ;
        this.velocidad = velocidad;
    }

    public void mover() {
        System.out.println("La entidad se mueve.");
    }

    public void moverA(double nuevaX, double nuevaY, double nuevaZ) {

        if (!Double.isFinite(nuevaX)
                || !Double.isFinite(nuevaY)
                || !Double.isFinite(nuevaZ)) {
            throw new IllegalArgumentException(
                    "La nueva posición debe contener valores válidos."
            );
        }

        this.posicionX = nuevaX;
        this.posicionY = nuevaY;
        this.posicionZ = nuevaZ;

        System.out.println(
                "La entidad se movió a (" +
                nuevaX + ", " +
                nuevaY + ", " +
                nuevaZ + ")."
        );
    }

    public void recibirDano(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de daño debe ser mayor que cero."
            );
        }

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Una entidad muerta no puede recibir más daño."
            );
        }

        salud = Math.max(0, salud - cantidad);

        System.out.println(
                "La entidad recibió " +
                cantidad +
                " de daño. Salud restante: " +
                salud
        );

        if (salud == 0) {
            morir();
        }
    }

    protected void morir() {
        System.out.println("La entidad ha muerto.");
    }

    public boolean estaViva() {
        return salud > 0;
    }

    public int getSalud() {
        return salud;
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
}
