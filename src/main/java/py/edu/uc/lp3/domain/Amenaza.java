package py.edu.uc.lp3.domain;

/**
 * Protocolo del dominio: algo que le puede hacer daño a una entidad.
 * <p>
 * Lo cumplen clases de dos familias que no tienen nada en común por
 * herencia: los mobs hostiles ({@link EntidadHostil}: {@link Zombie},
 * {@link Esqueleto}, {@link Creeper}) y las armas ({@link Arma}:
 * {@link Espada}, {@link Hacha}, que son {@link Herramienta}). Quien pregunta
 * "¿cuánto peligro corre esta entidad?" habla con {@code Amenaza} y no
 * necesita saber si del otro lado hay un zombie o una espada.
 * <p>
 * Los mensajes solo consultan: no cambian ni a la amenaza ni al objetivo.
 */
public interface Amenaza {

    /**
     * Daño que le haría ahora a {@code objetivo}; 0 si no lo puede dañar
     * (está lejos, alguno de los dos está muerto, el arma está rota...).
     */
    int danoContra(Entidad objetivo);

    /** Cómo hace daño, en una frase. */
    String describirAmenaza();
}
