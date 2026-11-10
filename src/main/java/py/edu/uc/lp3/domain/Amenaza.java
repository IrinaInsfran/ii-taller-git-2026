package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio: algo que le puede hacer daño a una entidad.
 * <p>
 * Lo cumplen clases de dos familias que no tienen nada en comun por
 * herencia: los mobs hostiles ({@link EntidadHostil}: {@link Zombie},
 * {@link Esqueleto}, {@link Creeper}) y las armas ({@link Arma}:
 * {@link Espada}, {@link Hacha}, que son {@link Herramienta}). Quien pregunta
 * "¿cuanto peligro corre esta entidad?" habla con {@code Amenaza} y no
 * necesita saber si del otro lado hay un zombie o una espada.
 * <p>
 * Los mensajes solo consultan: no cambian ni a la amenaza ni al objetivo.
 */
public interface Amenaza {

    /**
     * Daño que le haria ahora a {@code objetivo}; 0 si no lo puede dañar
     * (esta lejos, alguno de los dos esta muerto, el arma esta rota...).
     */
    int danoContra(Entidad objetivo);

    /** Como hace daño, en una frase. */
    String describirAmenaza();
}
