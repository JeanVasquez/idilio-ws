package com.idilio.idilio_app.app.util;

import com.idilio.idilio_app.app.model.*;
import com.idilio.idilio_app.app.repository.*;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final ParametroGlobalRepository parametroRepo;
    private final IngredienteRepository ingredienteRepo;
    private final RecetaRepository recetaRepo;
    private final ProductoRepository productoRepo;
    private final VentaRepository ventaRepo;

    public DataLoader(ParametroGlobalRepository parametroRepo,
                      IngredienteRepository ingredienteRepo,
                      RecetaRepository recetaRepo,
                      ProductoRepository productoRepo,
                      VentaRepository ventaRepo) {
        this.parametroRepo = parametroRepo;
        this.ingredienteRepo = ingredienteRepo;
        this.recetaRepo = recetaRepo;
        this.productoRepo = productoRepo;
        this.ventaRepo = ventaRepo;
    }

    @Override
    @Transactional
    public void run(@NonNull String... args) {

        // ============================================================
        // 1. CARGAR PARÁMETROS GLOBALES (uno por uno, sin duplicados)
        // ============================================================
        guardarParametroSiNoExiste("porcentaje_costo", "35.0", "Porcentaje del costo sobre el precio de venta");
        guardarParametroSiNoExiste("factor_redondeo", "500.0", "Múltiplo para redondear precios");
        guardarParametroSiNoExiste("comision_rappi_porcentaje", "10.0", "Comisión que cobra Rappi");
        guardarParametroSiNoExiste("iva_porcentaje", "0.0", "IVA (si aplica)");
        guardarParametroSiNoExiste("stock_seguridad_global", "0.0", "Stock mínimo adicional global");

        // ============================================================
        // 2. CARGAR INGREDIENTES (solo si no hay)
        // ============================================================
        if (ingredienteRepo.count() == 0) {
            Ingrediente harina = new Ingrediente(null, "Harina", "gr", 10000.0, 2000.0, 1.2);
            Ingrediente azucar = new Ingrediente(null, "Azúcar", "gr", 8000.0, 1500.0, 0.8);
            Ingrediente mantequilla = new Ingrediente(null, "Mantequilla", "gr", 5000.0, 1000.0, 2.5);
            Ingrediente huevo = new Ingrediente(null, "Huevo", "unidad", 100.0, 20.0, 400.0);
            Ingrediente cocoa = new Ingrediente(null, "Cocoa", "gr", 3000.0, 500.0, 3.0);
            Ingrediente leche = new Ingrediente(null, "Leche", "ml", 5000.0, 1000.0, 1.5);
            Ingrediente esenciaVainilla = new Ingrediente(null, "Esencia Vainilla", "ml", 1000.0, 200.0, 5.0);
            Ingrediente polvoHornear = new Ingrediente(null, "Polvo para hornear", "gr", 2000.0, 500.0, 2.0);
            Ingrediente sal = new Ingrediente(null, "Sal", "gr", 1000.0, 200.0, 1.0);
            Ingrediente chocolateChunks = new Ingrediente(null, "Chunks de chocolate", "gr", 3000.0, 500.0, 4.5);

            ingredienteRepo.saveAll(Arrays.asList(
                    harina, azucar, mantequilla, huevo, cocoa, leche,
                    esenciaVainilla, polvoHornear, sal, chocolateChunks
            ));
            System.out.println("✅ Ingredientes cargados.");
        }

        // ============================================================
        // 3. CARGAR RECETAS (usando los ingredientes ya creados)
        // ============================================================
        if (recetaRepo.count() == 0) {
            // Receta: Galleta base (rendimiento: 35 unidades)
            Receta galletaBase = new Receta();
            galletaBase.setNombre("Galleta base");
            galletaBase.setRendimiento(35);

            Ingrediente harina = ingredienteRepo.findByNombre("Harina").orElseThrow();
            Ingrediente azucar = ingredienteRepo.findByNombre("Azúcar").orElseThrow();
            Ingrediente mantequilla = ingredienteRepo.findByNombre("Mantequilla").orElseThrow();
            Ingrediente huevo = ingredienteRepo.findByNombre("Huevo").orElseThrow();
            Ingrediente polvo = ingredienteRepo.findByNombre("Polvo para hornear").orElseThrow();
            Ingrediente sal = ingredienteRepo.findByNombre("Sal").orElseThrow();
            Ingrediente vainilla = ingredienteRepo.findByNombre("Esencia Vainilla").orElseThrow();

            galletaBase.agregarIngrediente(harina, 500.0);
            galletaBase.agregarIngrediente(azucar, 200.0);
            galletaBase.agregarIngrediente(mantequilla, 250.0);
            galletaBase.agregarIngrediente(huevo, 2.0);
            galletaBase.agregarIngrediente(polvo, 5.0);
            galletaBase.agregarIngrediente(sal, 2.0);
            galletaBase.agregarIngrediente(vainilla, 5.0);
            recetaRepo.save(galletaBase);

            // Receta: Brownie (rendimiento: 20 unidades)
            Receta brownie = new Receta();
            brownie.setNombre("Brownie");
            brownie.setRendimiento(20);

            Ingrediente cocoa = ingredienteRepo.findByNombre("Cocoa").orElseThrow();
            Ingrediente chocolate = ingredienteRepo.findByNombre("Chunks de chocolate").orElseThrow();

            brownie.agregarIngrediente(harina, 300.0);
            brownie.agregarIngrediente(azucar, 400.0);
            brownie.agregarIngrediente(mantequilla, 300.0);
            brownie.agregarIngrediente(huevo, 4.0);
            brownie.agregarIngrediente(cocoa, 100.0);
            brownie.agregarIngrediente(chocolate, 200.0);
            brownie.agregarIngrediente(vainilla, 5.0);
            brownie.agregarIngrediente(sal, 2.0);
            brownie.agregarIngrediente(polvo, 5.0);
            recetaRepo.save(brownie);

            System.out.println("✅ Recetas cargadas.");
        }

        // ============================================================
        // 4. CARGAR PRODUCTOS
        // ============================================================
        if (productoRepo.count() == 0) {
            Receta galletaBase = recetaRepo.findByNombre("Galleta base").orElseThrow();
            Receta brownie = recetaRepo.findByNombre("Brownie").orElseThrow();

            // Producto simple: Galleta Red Velvet
            Producto galletaRV = new Producto();
            galletaRV.setNombre("Galleta Red Velvet");
            galletaRV.setDescripcion("Galleta suave con sabor a red velvet");
            galletaRV.setPrecioVenta(3000.0);
            galletaRV.setTipo(TipoProducto.SIMPLE);
            galletaRV.setReceta(galletaBase);
            galletaRV.setActivo(true);
            productoRepo.save(galletaRV);

            // Producto simple: Brownie
            Producto brownieProd = new Producto();
            brownieProd.setNombre("Brownie");
            brownieProd.setDescripcion("Brownie húmedo con chunks de chocolate");
            brownieProd.setPrecioVenta(3500.0);
            brownieProd.setTipo(TipoProducto.SIMPLE);
            brownieProd.setReceta(brownie);
            brownieProd.setActivo(true);
            productoRepo.save(brownieProd);

            // Producto combo: Combo Galletas (2 galletas + 1 brownie)
            Producto comboGalletas = new Producto();
            comboGalletas.setNombre("Combo Galletas");
            comboGalletas.setDescripcion("2 galletas + 1 brownie");
            comboGalletas.setPrecioVenta(8000.0);
            comboGalletas.setTipo(TipoProducto.COMBO);
            comboGalletas.setProductosHijos(Arrays.asList(galletaRV, galletaRV, brownieProd));
            comboGalletas.setActivo(true);
            productoRepo.save(comboGalletas);

            System.out.println("✅ Productos cargados.");
        }

        // ============================================================
        // 5. CARGAR VENTAS DE EJEMPLO (para reportes)
        // ============================================================
        if (ventaRepo.count() == 0) {
            Producto galletaRV = productoRepo.findByNombre("Galleta Red Velvet").orElseThrow();
            Producto brownieProd = productoRepo.findByNombre("Brownie").orElseThrow();
            Producto comboGalletas = productoRepo.findByNombre("Combo Galletas").orElseThrow();

            // Venta 1: Efectivo
            Venta venta1 = new Venta();
            venta1.setCliente("Cliente 1");
            venta1.setMetodoPago("EFECTIVO");
            venta1.setFecha(LocalDateTime.now().minusDays(2));
            venta1.setEstado(EstadoVenta.PAGADO);

            DetalleVenta det1 = new DetalleVenta();
            det1.setVenta(venta1);
            det1.setProducto(galletaRV);
            det1.setCantidad(3);
            det1.setPrecioUnitario(3000.0);
            det1.setSubtotal(9000.0);

            DetalleVenta det2 = new DetalleVenta();
            det2.setVenta(venta1);
            det2.setProducto(brownieProd);
            det2.setCantidad(2);
            det2.setPrecioUnitario(3500.0);
            det2.setSubtotal(7000.0);

            venta1.setTotal(16000.0);
            venta1.getDetalles().addAll(Arrays.asList(det1, det2));
            ventaRepo.save(venta1);

            // Venta 2: Nequi
            Venta venta2 = new Venta();
            venta2.setCliente("Cliente 2");
            venta2.setMetodoPago("NEQUI");
            venta2.setFecha(LocalDateTime.now().minusDays(1));
            venta2.setEstado(EstadoVenta.PAGADO);

            DetalleVenta det3 = new DetalleVenta();
            det3.setVenta(venta2);
            det3.setProducto(comboGalletas);
            det3.setCantidad(1);
            det3.setPrecioUnitario(8000.0);
            det3.setSubtotal(8000.0);

            venta2.setTotal(8000.0);
            venta2.getDetalles().add(det3);
            ventaRepo.save(venta2);

            // Venta 3: Rappi (con comisión)
            Venta venta3 = new Venta();
            venta3.setCliente("Cliente Rappi");
            venta3.setMetodoPago("RAPPI");
            venta3.setFecha(LocalDateTime.now());
            venta3.setEstado(EstadoVenta.PAGADO);

            DetalleVenta det4 = new DetalleVenta();
            det4.setVenta(venta3);
            det4.setProducto(galletaRV);
            det4.setCantidad(5);
            det4.setPrecioUnitario(3000.0);
            det4.setSubtotal(15000.0);

            double comision = 15000.0 * 0.10;
            venta3.setTotal(15000.0 - comision);
            venta3.getDetalles().add(det4);
            ventaRepo.save(venta3);

            System.out.println("✅ Ventas de ejemplo cargadas.");
        }

        System.out.println("🎉 DataLoader finalizado correctamente.");
    }

    private void guardarParametroSiNoExiste(String clave, String valor, String descripcion) {
        if (parametroRepo.findByClave(clave).isEmpty()) {
            parametroRepo.save(new ParametroGlobal(clave, valor, descripcion));
            System.out.println("✅ Parámetro '" + clave + "' cargado.");
        } else {
            System.out.println("ℹ️ Parámetro '" + clave + "' ya existe, omitido.");
        }
    }
}