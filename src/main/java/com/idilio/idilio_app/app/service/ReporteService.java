package com.idilio.idilio_app.app.service;

import com.idilio.idilio_app.app.dto.ProductoVendidoDTO;
import com.idilio.idilio_app.app.dto.StockBajoDTO;
import com.idilio.idilio_app.app.dto.VentaReporteDTO;
import com.idilio.idilio_app.app.model.DetalleVenta;
import com.idilio.idilio_app.app.model.Ingrediente;
import com.idilio.idilio_app.app.model.Venta;
import com.idilio.idilio_app.app.repository.IngredienteRepository;
import com.idilio.idilio_app.app.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final VentaRepository ventaRepo;

    private final IngredienteRepository ingredienteRepo;

    public ReporteService(VentaRepository ventaRepo, IngredienteRepository ingredienteRepo) {
        this.ventaRepo = ventaRepo;
        this.ingredienteRepo = ingredienteRepo;
    }

    // Ventas por rango de fechas
    public List<VentaReporteDTO> obtenerVentasPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Venta> ventas = ventaRepo.findByFechaBetween(inicio, fin);

        Map<String, VentaReporteDTO> reporteMap = new LinkedHashMap<>();

        for (Venta venta : ventas) {
            String fechaStr = venta.getFecha().toLocalDate().toString();
            VentaReporteDTO dto = reporteMap.getOrDefault(fechaStr, new VentaReporteDTO());
            dto.setFecha(fechaStr);
            Double totalActual = dto.getTotal() != null ? dto.getTotal() : 0.0;
            dto.setTotal(totalActual + venta.getTotal());
            int cantidadActual = dto.getCantidadVentas() != null ? dto.getCantidadVentas() : 0;
            dto.setCantidadVentas(cantidadActual + 1);
            reporteMap.put(fechaStr, dto);
        }

        return new ArrayList<>(reporteMap.values());
    }

    // Productos más vendidos
    public List<ProductoVendidoDTO> obtenerTopProductos(int limite) {
        List<Venta> ventas = ventaRepo.findAll();

        Map<Long, ProductoVendidoDTO> productMap = new HashMap<>();

        for (Venta venta : ventas) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Long productoId = detalle.getProducto().getId();
                ProductoVendidoDTO dto = productMap.getOrDefault(productoId, new ProductoVendidoDTO());
                dto.setProductoNombre(detalle.getProducto().getNombre());
                long cantidadActual = dto.getCantidadVendida() != null ? dto.getCantidadVendida() : 0L;
                dto.setCantidadVendida(cantidadActual + detalle.getCantidad());
                Double totalActual = dto.getTotalVendido() != null ? dto.getTotalVendido() : 0.0;
                dto.setTotalVendido(totalActual + detalle.getSubtotal());
                productMap.put(productoId, dto);
            }
        }

        return productMap.values().stream()
                .sorted((a, b) -> {
                    long cantB = a.getCantidadVendida() != null ? a.getCantidadVendida() : 0L;
                    long cantA = b.getCantidadVendida() != null ? b.getCantidadVendida() : 0L;
                    return Long.compare(cantA, cantB);
                })
                .limit(limite)
                .collect(Collectors.toList());
    }

    // Ingredientes con stock bajo
    public List<StockBajoDTO> obtenerStockBajo() {
        List<Ingrediente> ingredientes = ingredienteRepo.findAll();
        List<StockBajoDTO> result = new ArrayList<>();

        for (Ingrediente ing : ingredientes) {
            if (ing.getStock() < ing.getStockMinimo()) {
                StockBajoDTO dto = new StockBajoDTO();
                dto.setIngredienteNombre(ing.getNombre());
                dto.setStockActual(ing.getStock());
                dto.setStockMinimo(ing.getStockMinimo());
                result.add(dto);
            }
        }

        return result;
    }
}