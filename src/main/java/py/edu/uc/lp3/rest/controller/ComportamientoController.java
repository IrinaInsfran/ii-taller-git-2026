package py.edu.uc.lp3.ii_taller_git_2026.minecraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.ii_taller_git_2026.minecraft.Aldeano;
import py.edu.uc.lp3.ii_taller_git_2026.minecraft.Creeper;
import py.edu.uc.lp3.ii_taller_git_2026.minecraft.Entidad;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/minecraft")
public class ComportamientoController {

    @GetMapping("/comportamientos")
    public List<Map<String, String>> comportamientos() {

        Entidad creeper = new Creeper(
                20,
                6,
                64,
                0,
                2,
                16,
                3,
                30
        );

        Entidad aldeano = new Aldeano(
                20,
                10,
                64,
                0,
                2,
                false,
                "Herrero"
        );

        return List.of(
                describir(creeper),
                describir(aldeano)
        );
    }

    private Map<String, String> describir(Entidad entidad) {
        return Map.of(
                "tipo", entidad.getClass().getSimpleName(),
                "comportamiento", entidad.describirComportamiento()
        );
    }
}
