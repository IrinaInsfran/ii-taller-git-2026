package py.edu.uc.lp3.dto;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import py.edu.uc.lp3.domain.Amenaza;
import py.edu.uc.lp3.domain.Entidad;

/**
 * Cuanto peligro corre una entidad: cada {@link Amenaza} con el daño que le
 * haria ahora, y si sobreviviria a todas juntas.
 */
public record AmenazasResponse(
		Long objetivoId,
		TipoEntidad objetivoTipo,
		int saludActual,
		int danoTotal,
		boolean sobreviviria,
		List<Item> amenazas) {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Item(
			String origen,
			Long id,
			String descripcion,
			int dano) {
	}

	/**
	 * Arma el reporte hablando solo con el protocolo {@link Amenaza}: no
	 * pregunta si es un zombie o una espada para calcular el daño.
	 */
	public static AmenazasResponse evaluar(Entidad objetivo, List<? extends Amenaza> amenazas) {
		List<Item> items = amenazas.stream()
				.map(amenaza -> new Item(
						amenaza.getClass().getSimpleName(),
						// Solo para identificarla en el JSON: las armas no tienen id.
						amenaza instanceof Entidad entidad ? entidad.getId() : null,
						amenaza.describirAmenaza(),
						amenaza.danoContra(objetivo)))
				.sorted(Comparator.comparingInt(Item::dano).reversed())
				.toList();

		int total = items.stream().mapToInt(Item::dano).sum();

		return new AmenazasResponse(
				objetivo.getId(),
				TipoEntidad.de(objetivo),
				objetivo.getSalud(),
				total,
				total < objetivo.getSalud(),
				items);
	}
}
