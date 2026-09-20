package py.edu.uc.lp3.herencia;

public class Main {

    public static void main(String[] args) {

        Jugador jugador = new Jugador(
                20,
                0,
                64,
                0,
                5,
                "Steve",
                0
        );

        Zombie zombie = new Zombie(
                20,
                5,
                64,
                0,
                3,
                16,
                3
        );

        Creeper creeper = new Creeper(
                20,
                6,
                64,
                0,
                2,
                16,
                3,
                30
        );

        Aldeano aldeano = new Aldeano(
                20,
                10,
                64,
                0,
                2,
                false,
                "Herrero"
        );

        Animal vaca = new Animal(
                10,
                12,
                64,
                0,
                2,
                true,
                true
        );

        jugador.construir();
        jugador.craftear();
        jugador.interactuar();

        zombie.atacar(jugador);
        zombie.infectaAldeano();

        creeper.explotar();

        aldeano.comercio();
        aldeano.huir(zombie);

        System.out.println(
                "La vaca es montable: " + vaca.isMontar()
        );
    }
}
