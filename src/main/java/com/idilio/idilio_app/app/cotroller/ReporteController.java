package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.dto.ProductoVendidoDTO;
import com.idilio.idilio_app.app.dto.StockBajoDTO;
import com.idilio.idilio_app.app.dto.VentaReporteDTO;
import com.idilio.idilio_app.app.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/ventas")
    public List<VentaReporteDTO> ventasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        return reporteService.obtenerVentasPorFecha(fechaInicio, fechaFin);
    }

    @GetMapping("/top-productos")
    public List<ProductoVendidoDTO> topProductos(
            @RequestParam(defaultValue = "5") int limite) {
        return reporteService.obtenerTopProductos(limite);
    }

    @GetMapping("/stock-bajo")
    public List<StockBajoDTO> stockBajo() {
        return reporteService.obtenerStockBajo();
    }
}