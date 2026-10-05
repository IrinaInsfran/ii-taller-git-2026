package py.edu.uc.lp3.service;

import py.edu.uc.lp3.minecraft.Creeper;

/*
 * Servicio que se encarga de construir creepers
 * a partir de los datos recibidos por la API REST
 */
public interface CreeperService {

	/*
	 * Funcion para crear un creeper con las caracteristicas indicadas
	 * Parametros:
	 * 				salud, x, y, z, velocidad, rangoDeteccion, danoAtaque, tiempoExplosion
	 * Retorno:
	 * 				Creeper : el creeper creado
	 * */
	Creeper crear(int salud,
	              double x,
	              double y,
	              double z,
	              int velocidad,
	              double rangoDeteccion,
	              int danoAtaque,
	              int tiempoExplosion);

	/*
	 * Funcion para crear un creeper en la posicion indicada usando
	 * los valores por defecto del constructor sobrecargado
	 * Parametros:
	 * 				x, y, z
	 * Retorno:
	 * 				Creeper : el creeper creado
	 * */
	Creeper crearPorPosicion(double x, double y, double z);

}