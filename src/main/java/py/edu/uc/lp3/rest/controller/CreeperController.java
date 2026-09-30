package py.edu.uc.lp3.ii_taller_git_2026.minecraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.ii_taller_git_2026.minecraft.Creeper;

@RestController
@RequestMapping("/api/minecraft")
public class CreeperController {

    @GetMapping("/creeper")
    public Creeper crearCreeper(
            @RequestParam(name = "salud") int salud,
            @RequestParam(name = "x") double x,
            @RequestParam(name = "y") double y,
            @RequestParam(name = "z") double z,
            @RequestParam(name = "velocidad") int velocidad,
            @RequestParam(name = "rangoDeteccion") double rangoDeteccion,
            @RequestParam(name = "danoAtaque") int danoAtaque,
            @RequestParam(name = "tiempoExplosion") int tiempoExplosion) {

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
