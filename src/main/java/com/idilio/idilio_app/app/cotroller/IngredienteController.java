package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.model.Ingrediente;
import com.idilio.idilio_app.app.service.IngredienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<Ingrediente>> listarTodos() {
        return ResponseEntity.ok(ingredienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingrediente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingredienteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Ingrediente> crear(@RequestBody Ingrediente ingrediente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteService.guardar(ingrediente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingrediente> actualizar(@PathVariable Long id, @RequestBody Ingrediente ingrediente) {
        ingrediente.setId(id); // Asegura que el ID sea el de la ruta
        return ResponseEntity.ok(ingredienteService.guardar(ingrediente));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Ingrediente> ajustarStock(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        Double nuevoStock = payload.get("stock");
        if (nuevoStock == null) {
            throw new RuntimeException("El campo 'stock' es requerido");
        }
        ingredienteService.ajustarStock(id, nuevoStock);
        return ResponseEntity.ok(ingredienteService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
