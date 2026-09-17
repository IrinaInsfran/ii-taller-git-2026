package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Clase base que representa cualquier entidad del mundo del juego.
 *
 * Invariantes (se cumplen después del constructor y de cada mensaje):
 * - 0 <= salud <= saludMaxima
 * - velocidad > 0, y en un paso no se mueve más bloques que su velocidad
 * - una entidad muerta no se mueve ni actúa
 */
public abstract class Entidad {

    private final int saludMaxima;
    private int salud;
    private double posicionX;
    private double posicionY;
    private double posicionZ;
    private final int velocidad;

    protected Entidad(int salud, double posicionX, double posicionY, double posicionZ, int velocidad) {
        if (salud <= 0) throw new IllegalArgumentException("la salud inicial debe ser mayor a 0");
        if (velocidad <= 0) throw new IllegalArgumentException("la velocidad debe ser mayor a 0");
        this.saludMaxima = salud;
        this.salud = salud;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.posicionZ = posicionZ;
        this.velocidad = velocidad;
    }

    /** Mueve la entidad. No puede avanzar más bloques que su velocidad en un solo paso. */
    public String mover(double dx, double dy, double dz) {
        exigirViva();
        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distancia > velocidad + 1e-9) {
            throw new IllegalArgumentException(getTipo() + " no puede moverse más de " + velocidad + " bloques por paso");
        }
        posicionX += dx;
        posicionY += dy;
        posicionZ += dz;
        return getTipo() + " se mueve a " + getPosicion();
    }

    /** final: ninguna subclase puede saltarse la regla de salud. */
    public final String recibirDano(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("el daño no puede ser negativo");
        if (!estaViva()) return getTipo() + " ya no tiene salud.";
        int real = Math.max(0, ajustarDano(cantidad));   // una hija tampoco puede "curar" devolviendo negativo
        salud = Math.max(0, salud - real);
        String mensaje = getTipo() + " recibió " + real + " de daño. Salud restante: " + salud + ".";
        if (salud == 0) {
            mensaje += " " + alMorir();
        }
        return mensaje;
    }

    /** final: la salud nunca supera la máxima y un muerto no se cura. */
    public final String curar(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("la curación no puede ser negativa");
        exigirViva();
        salud = Math.min(saludMaxima, salud + cantidad);
        return getTipo() + " se cura. Salud: " + salud + "/" + saludMaxima;
    }

    /** Gancho: una subclase puede modificar el daño recibido (por ejemplo, armadura). */
    protected int ajustarDano(int cantidad) {
        return cantidad;
    }

    /** Gancho: qué pasa cuando la entidad muere. */
    protected String alMorir() {
        return getTipo() + " ha muerto.";
    }

    /** Gancho para las subclases: toda acción empieza verificando que la entidad siga viva. */
    protected final void exigirViva() {
        if (!estaViva()) throw new IllegalStateException(getTipo() + " no puede actuar: su salud es 0");
    }

    public double distanciaA(Entidad otra) {
        if (otra == null) throw new IllegalArgumentException("la otra entidad es requerida");
        double dx = posicionX - otra.posicionX;
        double dy = posicionY - otra.posicionY;
        double dz = posicionZ - otra.posicionZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public boolean estaViva() {
        return salud > 0;
    }

    /** Nombre del tipo concreto, sin necesidad de un if por cada clase. */
    public String getTipo() {
        return getClass().getSimpleName();
    }

    /** Información propia de cada tipo; las subclases la redefinen. */
    public String detalle() {
        return "";
    }

    // Solo getters: el estado cambia por mensajes, no por setters.

    public int getSalud() { return salud; }
    public int getSaludMaxima() { return saludMaxima; }
    public double getPosicionX() { return posicionX; }
    public double getPosicionY() { return posicionY; }
    public double getPosicionZ() { return posicionZ; }
    public int getVelocidad() { return velocidad; }

    public String getPosicion() {
        return "(" + posicionX + ", " + posicionY + ", " + posicionZ + ")";
    }
}
