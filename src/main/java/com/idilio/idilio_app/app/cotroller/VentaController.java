package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.dto.VentaRequestDTO;
import com.idilio.idilio_app.app.dto.VentaResponseDTO;
import com.idilio.idilio_app.app.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> registrarVenta(@RequestBody VentaRequestDTO request) {
        VentaResponseDTO response = ventaService.registrarVenta(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<VentaResponseDTO> listarTodas() {
        return ventaService.listarTodas();
    }

    @GetMapping("/{id}")
    public VentaResponseDTO obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VentaResponseDTO> cancelarVenta(@PathVariable Long id) {
        VentaResponseDTO response = ventaService.cancelarVenta(id);
        return ResponseEntity.ok(response);
    }
}