package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.dto.RecetaDTO;
import com.idilio.idilio_app.app.model.Receta;
import com.idilio.idilio_app.app.service.RecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public List<Receta> listar() {
        return recetaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Receta obtener(@PathVariable Long id) {
        return recetaService.buscarPorId(id);
    }

    @GetMapping("/{id}/costo")
    public ResponseEntity<Double> calcularCosto(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.calcularCostoUnitario(id));
    }

    @PostMapping
    public Receta crear(@RequestBody RecetaDTO dto) {
        return recetaService.crear(dto);
    }

    @PutMapping("/{id}")
    public Receta actualizar(@PathVariable Long id, @RequestBody RecetaDTO dto) {
        return recetaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
