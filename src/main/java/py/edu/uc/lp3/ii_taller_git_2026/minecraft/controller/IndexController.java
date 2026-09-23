package py.edu.uc.lp3.ii_taller_git_2026.minecraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
                "autora", "Irina",
                "dominio", "Minecraft",
                "estado", "API funcionando"
        );
    }
}
