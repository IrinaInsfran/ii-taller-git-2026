package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Clase base que representa cualquier entidad del mundo del juego.
 */
public abstract class Entidad {

    private int salud;
    private final int saludMaxima;
    private double posicionX;
    private double posicionY;
    private double posicionZ;
    private final int velocidad;

    public Entidad(int salud, double posicionX, double posicionY,
                   double posicionZ, int velocidad) {

        if (salud <= 0) {
            throw new IllegalArgumentException("La salud debe ser mayor que cero.");
        }

        if (velocidad < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa.");
        }

        if (!Double.isFinite(posicionX)
                || !Double.isFinite(posicionY)
                || !Double.isFinite(posicionZ)) {
            throw new IllegalArgumentException("La posición debe contener valores válidos.");
        }

        this.salud = salud;
        this.saludMaxima = salud;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.posicionZ = posicionZ;
        this.velocidad = velocidad;
    }

    /**
     * Mantiene compatibilidad con el comportamiento original.
     */
    public void mover() {
        if (!estaViva()) {
            throw new IllegalStateException("Una entidad muerta no puede moverse.");
        }
        System.out.println("La entidad se mueve.");
    }

    /**
     * Desplaza la entidad una cantidad (dx, dy, dz).
     * La magnitud del desplazamiento no puede superar su velocidad.
     */
    public void mover(double dx, double dy, double dz) {

        if (!estaViva()) {
            throw new IllegalStateException("Una entidad muerta no puede moverse.");
        }

        if (!Double.isFinite(dx) || !Double.isFinite(dy) || !Double.isFinite(dz)) {
            throw new IllegalArgumentException("El desplazamiento debe contener valores válidos.");
        }

        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distancia > velocidad + 1e-9) {
            throw new IllegalArgumentException(
                    "La entidad no puede moverse más que su velocidad."
            );
        }

        posicionX += dx;
        posicionY += dy;
        posicionZ += dz;
    }

    public double distanciaA(Entidad otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La otra entidad no puede ser nula.");
        }

        double dx = otra.posicionX - posicionX;
        double dy = otra.posicionY - posicionY;
        double dz = otra.posicionZ - posicionZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
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

        if (salud == 0) {
            morir();
        }
    }

    public void curar(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de curación debe ser mayor que cero."
            );
        }

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Una entidad muerta no puede ser curada."
            );
        }

        salud = Math.min(saludMaxima, salud + cantidad);
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
    public abstract String describirComportamiento();
}
