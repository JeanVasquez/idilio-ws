package com.idilio.idilio_app.app.cotroller;

import com.idilio.idilio_app.app.model.Ingrediente;
import com.idilio.idilio_app.app.model.Producto;
import com.idilio.idilio_app.app.model.Venta;
import com.idilio.idilio_app.app.service.IngredienteService;
import com.idilio.idilio_app.app.service.ProductoService;
import com.idilio.idilio_app.app.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IngredienteService ingredienteService;

    @GetMapping("/")
    public String dashboard(Model model) {
        // Ventas de hoy
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = LocalDate.now().atTime(23, 59, 59);
        List<Venta> ventasHoy = ventaService.obtenerVentasPorFecha(inicio, fin);

        // Resumen
        model.addAttribute("ventasHoy", ventasHoy.size());
        double ingresosHoy = ventasHoy.stream().mapToDouble(Venta::getTotal).sum();
        model.addAttribute("ingresosHoy", ingresosHoy);

        // Productos activos
        List<Producto> productos = productoService.listarTodos(true);
        model.addAttribute("productosActivos", productos.size());

        // Stock bajo
        List<Ingrediente> stockBajo = ingredienteService.listarStockBajo();
        model.addAttribute("stockBajo", stockBajo.size());

        // Últimas 5 ventas
        List<Venta> ultimasVentas = ventaService.listarUltimas(5);
        model.addAttribute("ultimasVentas", ultimasVentas);

        return "index";
    }
}
