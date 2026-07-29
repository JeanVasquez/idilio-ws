package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.model.ParametroGlobal;
import com.idilio.idilio_app.app.service.ParametroGlobalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ParametroGlobalService parametroService;

    public ConfiguracionController(ParametroGlobalService parametroService) {
        this.parametroService = parametroService;
    }

    @GetMapping
    public List<ParametroGlobal> listar() {
        return parametroService.listarTodos();
    }

    @GetMapping("/{clave}")
    public ParametroGlobal obtener(@PathVariable String clave) {
        return parametroService.obtenerPorClave(clave);
    }

    @PostMapping
    public ParametroGlobal crear(@RequestBody ParametroGlobal parametro) {
        return parametroService.crear(parametro);
    }

    @PutMapping("/{clave}")
    public ParametroGlobal actualizar(@PathVariable String clave, @RequestBody ParametroGlobal parametro) {
        return parametroService.actualizar(clave, parametro.getValor());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        parametroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}