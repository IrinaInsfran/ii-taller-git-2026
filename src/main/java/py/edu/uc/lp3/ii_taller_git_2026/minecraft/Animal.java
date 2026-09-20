package py.edu.uc.lp3.herencia;

/**
 * Animal: entidad pasiva que puede ser montable.
 */
public class Animal extends EntidadPasiva {

    private final boolean montar;

    public Animal(int salud,
                  double posicionX,
                  double posicionY,
                  double posicionZ,
                  int velocidad,
                  boolean domesticable,
                  boolean montar) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                domesticable
        );

        this.montar = montar;
    }

    public boolean isMontar() {
        return montar;
    }
}
