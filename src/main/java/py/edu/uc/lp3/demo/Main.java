package py.edu.uc.lp3.demo;

import java.util.List;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.service.AccionService;
import py.edu.uc.lp3.service.ReglasCreeperService;
import py.edu.uc.lp3.service.impl.AccionServiceImpl;
import py.edu.uc.lp3.service.impl.HerramientaServiceImpl;
import py.edu.uc.lp3.service.impl.ReglasCreeperServiceImpl;

/**
 * Demostración por consola, sin Spring ni base de datos: las entidades solo
 * guardan el estado y las acciones pasan por los servicios, que aplican las
 * reglas.
 */
public class Main {

    public static void main(String[] args) {

        AccionService acciones = new AccionServiceImpl(new HerramientaServiceImpl());
        ReglasCreeperService reglasCreeper = new ReglasCreeperServiceImpl();

        Jugador jugador = new Jugador(20, 0, 64, 0, 5, "Steve", 0);
        Zombie zombie = new Zombie(20, 5, 64, 0, 3, 16, 3);
        Creeper creeper = new Creeper(20, 2, 64, 0, 2, 16, 3, 30);
        Aldeano aldeano = new Aldeano(20, 10, 64, 0, 2, false, "Herrero");
        Animal caballo = new Animal(10, 12, 64, 0, 2, true, true);

        acciones.atacar(zombie, jugador);
        System.out.println("El zombie ataca: Steve queda con " + jugador.getSalud() + " de salud.");

        System.out.println(acciones.comerciar(aldeano));
        acciones.infectar(zombie, aldeano);
        System.out.println("El aldeano quedó infectado: " + aldeano.isInfectado());

        var explosion = reglasCreeper.explotarCercaDe(
                creeper, List.of(jugador), List.of(jugador, zombie, aldeano, caballo));
        System.out.println("El creeper explotó: alcanzó a " + explosion.getEntidadesAlcanzadas()
                + " entidades; Steve queda con " + jugador.getSalud() + " de salud.");

        System.out.println(acciones.montar(caballo, jugador));
    }
}
