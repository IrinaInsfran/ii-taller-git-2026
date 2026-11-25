package py.edu.uc.lp3.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Una jugada del escenario de POO-09 que A2 y A3 aceptaron: qué creeper
 * explotó, qué explosión se guardó y qué contestó cada compañera. Solo se
 * crea cuando las dos aceptan, así que una fila de esta tabla siempre es
 * una jugada válida para el grupo.
 */
@Entity
public class Jugada {

    /** Largo máximo que se guarda de la respuesta de una compañera. */
    public static final int LARGO_RESPUESTA = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long creeperId;
    private Long explosionId;
    private int tiempoTranscurrido;
    private Instant momento;

    private String companeraA2;
    private String urlA2;
    private int estadoA2;
    @Column(length = LARGO_RESPUESTA)
    private String respuestaA2;

    private String companeraA3;
    private String urlA3;
    private int estadoA3;
    @Column(length = LARGO_RESPUESTA)
    private String respuestaA3;

    protected Jugada() {
    }

    public Jugada(Long creeperId, Long explosionId, int tiempoTranscurrido, Instant momento,
                  String companeraA2, String urlA2, int estadoA2, String respuestaA2,
                  String companeraA3, String urlA3, int estadoA3, String respuestaA3) {
        this.creeperId = creeperId;
        this.explosionId = explosionId;
        this.tiempoTranscurrido = tiempoTranscurrido;
        this.momento = momento;
        this.companeraA2 = companeraA2;
        this.urlA2 = urlA2;
        this.estadoA2 = estadoA2;
        this.respuestaA2 = respuestaA2;
        this.companeraA3 = companeraA3;
        this.urlA3 = urlA3;
        this.estadoA3 = estadoA3;
        this.respuestaA3 = respuestaA3;
    }

    public Long getId() {
        return id;
    }

    public Long getCreeperId() {
        return creeperId;
    }

    public Long getExplosionId() {
        return explosionId;
    }

    public int getTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public Instant getMomento() {
        return momento;
    }

    public String getCompaneraA2() {
        return companeraA2;
    }

    public String getUrlA2() {
        return urlA2;
    }

    public int getEstadoA2() {
        return estadoA2;
    }

    public String getRespuestaA2() {
        return respuestaA2;
    }

    public String getCompaneraA3() {
        return companeraA3;
    }

    public String getUrlA3() {
        return urlA3;
    }

    public int getEstadoA3() {
        return estadoA3;
    }

    public String getRespuestaA3() {
        return respuestaA3;
    }
}
