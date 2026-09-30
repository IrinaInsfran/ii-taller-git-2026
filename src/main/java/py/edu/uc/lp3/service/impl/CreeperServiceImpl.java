package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.service.CreeperService;

@Service
public class CreeperServiceImpl implements CreeperService {

	@Override
	public Creeper crear(int salud,
	                    double x,
	                    double y,
	                    double z,
	                    int velocidad,
	                    double rangoDeteccion,
	                    int danoAtaque,
	                    int tiempoExplosion) {

		return new Creeper(
				salud,
				x,
				y,
				z,
				velocidad,
				rangoDeteccion,
				danoAtaque,
				tiempoExplosion
		);
	}

}