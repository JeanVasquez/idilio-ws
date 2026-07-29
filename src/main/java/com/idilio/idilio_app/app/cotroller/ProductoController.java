package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.dto.ProductoDTO;
import com.idilio.idilio_app.app.model.Producto;
import com.idilio.idilio_app.app.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) Boolean activos) {
        return productoService.listarTodos(activos);
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @GetMapping("/{id}/costo")
    public ResponseEntity<Double> calcularCosto(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(productoService.calcularCostoUnitario(producto));
    }

    @GetMapping("/{id}/precio-sugerido")
    public ResponseEntity<Double> calcularPrecioSugerido(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.calcularPrecioSugerido(id));
    }

    @PostMapping
    public Producto crear(@RequestBody ProductoDTO dto) {
        return productoService.crear(dto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        return productoService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public Producto cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return productoService.cambiarEstado(id, activo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
