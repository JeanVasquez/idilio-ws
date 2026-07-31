package com.idilio.idilio_app.app.util;

import com.idilio.idilio_app.app.model.*;
import com.idilio.idilio_app.app.repository.*;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final ParametroGlobalRepository parametroRepo;
    private final IngredienteRepository ingredienteRepo;
    private final RecetaRepository recetaRepo;
    private final ProductoRepository productoRepo;
    private final VentaRepository ventaRepo;
    private final Random random = new Random();

    // Listas para referencias cruzadas
    private List<Ingrediente> ingredientes;
    private List<Receta> recetas;
    private List<Producto> productos;
    private List<String> clientes = Arrays.asList(
            "María Pérez", "Juan Rodríguez", "Ana Gómez", "Carlos López",
            "Laura Martínez", "Pedro Sánchez", "Sofía Ramírez", "Diego Torres",
            "Valentina Herrera", "Andrés Castro", "Isabel Romero", "Felipe Díaz",
            "Camila Suárez", "Nicolás Muñoz", "Daniela Rojas", "Mateo Jiménez",
            "Natalia Vargas", "Simón Ortiz", "Gabriela Medina", "Alejandro Ríos",
            "Karina Zambrano", "Sergio Paredes", "Paula Acosta", "Ricardo Cárdenas",
            "Ximena Lozano", "Julián Salazar", "Monica Cabrera", "Hugo Santana"
    );
    private List<String> metodosPago = Arrays.asList("EFECTIVO", "NEQUI", "BANCOLOMBIA", "RAPPI", "EMILLY", "GISELE");

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
        System.out.println("🚀 Iniciando DataLoader...");

        cargarParametros();
        cargarIngredientes();
        cargarRecetas();
        cargarProductos();
        cargarVentasDelMes();

        System.out.println("🎉 DataLoader finalizado correctamente.");
    }

    // ============================================================
    // 1. PARÁMETROS GLOBALES
    // ============================================================
    private void cargarParametros() {
        guardarParametroSiNoExiste("porcentaje_costo", "35.0", "Porcentaje del costo sobre el precio de venta");
        guardarParametroSiNoExiste("factor_redondeo", "500.0", "Múltiplo para redondear precios");
        guardarParametroSiNoExiste("comision_rappi_porcentaje", "10.0", "Comisión que cobra Rappi");
        guardarParametroSiNoExiste("iva_porcentaje", "0.0", "IVA (si aplica)");
        guardarParametroSiNoExiste("stock_seguridad_global", "0.0", "Stock mínimo adicional global");
    }

    private void guardarParametroSiNoExiste(String clave, String valor, String descripcion) {
        if (parametroRepo.findByClave(clave).isEmpty()) {
            parametroRepo.save(new ParametroGlobal(clave, valor, descripcion));
            System.out.println("✅ Parámetro '" + clave + "' cargado.");
        }
    }

    // ============================================================
    // 2. INGREDIENTES (más de 30, con precios en COP)
    // ============================================================
    private void cargarIngredientes() {
        if (ingredienteRepo.count() > 0) {
            System.out.println("ℹ️ Ingredientes ya existen, omitiendo carga.");
            // Cargar la lista desde la BD para referencias posteriores
            ingredientes = ingredienteRepo.findAll();
            return;
        }

        List<Ingrediente> lista = new ArrayList<>();

        // HARINAS
        lista.add(new Ingrediente(null, "Harina de trigo", "gr", 15000.0, 2000.0, 1.0));
        lista.add(new Ingrediente(null, "Harina de almendra", "gr", 3000.0, 500.0, 6.0));
        lista.add(new Ingrediente(null, "Harina integral", "gr", 5000.0, 800.0, 1.5));
        lista.add(new Ingrediente(null, "Maicena", "gr", 4000.0, 600.0, 2.0));

        // AZÚCARES Y ENDULZANTES
        lista.add(new Ingrediente(null, "Azúcar blanca", "gr", 12000.0, 1500.0, 0.7));
        lista.add(new Ingrediente(null, "Azúcar morena", "gr", 8000.0, 1000.0, 0.9));
        lista.add(new Ingrediente(null, "Azúcar pulverizada", "gr", 5000.0, 800.0, 1.2));
        lista.add(new Ingrediente(null, "Panela rallada", "gr", 3000.0, 400.0, 1.8));

        // GRASAS
        lista.add(new Ingrediente(null, "Mantequilla", "gr", 8000.0, 1000.0, 2.5));
        lista.add(new Ingrediente(null, "Margarina", "gr", 6000.0, 800.0, 2.0));
        lista.add(new Ingrediente(null, "Aceite vegetal", "ml", 5000.0, 600.0, 1.2));

        // LÁCTEOS
        lista.add(new Ingrediente(null, "Leche entera", "ml", 8000.0, 1200.0, 0.9));
        lista.add(new Ingrediente(null, "Leche condensada", "ml", 3000.0, 400.0, 2.5));
        lista.add(new Ingrediente(null, "Crema de leche", "ml", 2000.0, 300.0, 3.5));
        lista.add(new Ingrediente(null, "Queso crema", "gr", 2500.0, 400.0, 4.0));
        lista.add(new Ingrediente(null, "Yogurt natural", "ml", 2000.0, 300.0, 1.2));

        // HUEVOS
        lista.add(new Ingrediente(null, "Huevo", "unidad", 150.0, 20.0, 400.0));

        // CHOCOLATE Y DERIVADOS
        lista.add(new Ingrediente(null, "Cocoa en polvo", "gr", 4000.0, 500.0, 3.0));
        lista.add(new Ingrediente(null, "Chocolate negro", "gr", 3000.0, 400.0, 5.0));
        lista.add(new Ingrediente(null, "Chocolate blanco", "gr", 2000.0, 300.0, 6.0));
        lista.add(new Ingrediente(null, "Chunks de chocolate", "gr", 3000.0, 500.0, 4.5));
        lista.add(new Ingrediente(null, "Galletas Oreo trituradas", "gr", 2000.0, 300.0, 3.5));

        // FRUTAS
        lista.add(new Ingrediente(null, "Fresas", "gr", 2000.0, 300.0, 2.5));
        lista.add(new Ingrediente(null, "Arándanos", "gr", 1500.0, 200.0, 4.0));
        lista.add(new Ingrediente(null, "Banano", "unidad", 100.0, 20.0, 500.0));
        lista.add(new Ingrediente(null, "Naranja", "unidad", 80.0, 15.0, 600.0));
        lista.add(new Ingrediente(null, "Limón", "unidad", 60.0, 10.0, 700.0));
        lista.add(new Ingrediente(null, "Coco deshidratado", "gr", 2000.0, 300.0, 2.0));
        lista.add(new Ingrediente(null, "Mermelada de frutos rojos", "gr", 1000.0, 150.0, 4.0));

        // FRUTOS SECOS
        lista.add(new Ingrediente(null, "Almendras", "gr", 1500.0, 200.0, 8.0));
        lista.add(new Ingrediente(null, "Nueces", "gr", 1000.0, 150.0, 9.0));
        lista.add(new Ingrediente(null, "Pistachos", "gr", 800.0, 100.0, 12.0));

        // ESENCIAS Y SABORIZANTES
        lista.add(new Ingrediente(null, "Esencia de vainilla", "ml", 500.0, 80.0, 10.0));
        lista.add(new Ingrediente(null, "Esencia de café", "ml", 300.0, 50.0, 12.0));
        lista.add(new Ingrediente(null, "Esencia de coco", "ml", 300.0, 50.0, 11.0));
        lista.add(new Ingrediente(null, "Colorante rojo", "ml", 200.0, 30.0, 15.0));
        lista.add(new Ingrediente(null, "Colorante azul", "ml", 200.0, 30.0, 15.0));

        // OTROS
        lista.add(new Ingrediente(null, "Polvo para hornear", "gr", 2500.0, 400.0, 1.0));
        lista.add(new Ingrediente(null, "Bicarbonato de sodio", "gr", 500.0, 80.0, 0.8));
        lista.add(new Ingrediente(null, "Sal", "gr", 1500.0, 200.0, 0.5));
        lista.add(new Ingrediente(null, "Canela en polvo", "gr", 500.0, 80.0, 4.0));
        lista.add(new Ingrediente(null, "Jengibre en polvo", "gr", 300.0, 50.0, 5.0));
        lista.add(new Ingrediente(null, "Café instantáneo", "gr", 1000.0, 150.0, 8.0));
        lista.add(new Ingrediente(null, "Zanahoria", "gr", 1000.0, 150.0, 2.0));

        lista.add(new Ingrediente(null, "Gelatina sin sabor", "gr", 200.0, 30.0, 3.0));
        lista.add(new Ingrediente(null, "Crema chantilly", "ml", 1000.0, 150.0, 5.0));
        lista.add(new Ingrediente(null, "Miel", "ml", 500.0, 80.0, 6.0));

        ingredientes = ingredienteRepo.saveAll(lista);
        System.out.println("✅ " + ingredientes.size() + " ingredientes cargados.");
    }

    // ============================================================
    // 3. RECETAS (10 recetas variadas)
    // ============================================================
    private void cargarRecetas() {
        if (recetaRepo.count() > 0) {
            System.out.println("ℹ️ Recetas ya existen, omitiendo carga.");
            recetas = recetaRepo.findAll();
            return;
        }

        // Obtener ingredientes por nombre para las recetas
        Ingrediente harinaTrigo = getIngrediente("Harina de trigo");
        Ingrediente azucarBlanca = getIngrediente("Azúcar blanca");
        Ingrediente azucarMorena = getIngrediente("Azúcar morena");
        Ingrediente azucarPulverizada = getIngrediente("Azúcar pulverizada");
        Ingrediente mantequilla = getIngrediente("Mantequilla");
        Ingrediente huevo = getIngrediente("Huevo");
        Ingrediente cocoa = getIngrediente("Cocoa en polvo");
        Ingrediente chocolateNegro = getIngrediente("Chocolate negro");
        Ingrediente chocolateBlanco = getIngrediente("Chocolate blanco");
        Ingrediente chunks = getIngrediente("Chunks de chocolate");
        Ingrediente vainilla = getIngrediente("Esencia de vainilla");
        Ingrediente polvo = getIngrediente("Polvo para hornear");
        Ingrediente bicarbonato = getIngrediente("Bicarbonato de sodio");
        Ingrediente sal = getIngrediente("Sal");
        Ingrediente leche = getIngrediente("Leche entera");
        Ingrediente lecheCondensada = getIngrediente("Leche condensada");
        Ingrediente crema = getIngrediente("Crema de leche");
        Ingrediente mermelada = getIngrediente("Mermelada de frutos rojos");
        Ingrediente fresas = getIngrediente("Fresas");
        Ingrediente arandanos = getIngrediente("Arándanos");
        Ingrediente banano = getIngrediente("Banano");
        Ingrediente naranja = getIngrediente("Naranja");
        Ingrediente limon = getIngrediente("Limón");
        Ingrediente coco = getIngrediente("Coco deshidratado");
        Ingrediente esenciaCafe = getIngrediente("Esencia de café");
        Ingrediente coloranteRojo = getIngrediente("Colorante rojo");
        Ingrediente nueces = getIngrediente("Nueces");
        Ingrediente almendras = getIngrediente("Almendras");
        Ingrediente zanahoria = getIngrediente("Zanahoria");
        Ingrediente cafeInstantaneo = getIngrediente("Café instantáneo");

        List<Receta> lista = new ArrayList<>();

        // 1. Galleta base (vainilla)
        Receta galletaBase = new Receta();
        galletaBase.setNombre("Galleta base");
        galletaBase.setRendimiento(35);
        galletaBase.agregarIngrediente(harinaTrigo, 500.0);
        galletaBase.agregarIngrediente(azucarBlanca, 200.0);
        galletaBase.agregarIngrediente(mantequilla, 250.0);
        galletaBase.agregarIngrediente(huevo, 2.0);
        galletaBase.agregarIngrediente(polvo, 5.0);
        galletaBase.agregarIngrediente(sal, 2.0);
        galletaBase.agregarIngrediente(vainilla, 5.0);
        lista.add(galletaBase);

        // 2. Galleta de chocolate
        Receta galletaChocolate = new Receta();
        galletaChocolate.setNombre("Galleta de chocolate");
        galletaChocolate.setRendimiento(35);
        galletaChocolate.agregarIngrediente(harinaTrigo, 400.0);
        galletaChocolate.agregarIngrediente(azucarMorena, 250.0);
        galletaChocolate.agregarIngrediente(mantequilla, 250.0);
        galletaChocolate.agregarIngrediente(huevo, 2.0);
        galletaChocolate.agregarIngrediente(cocoa, 80.0);
        galletaChocolate.agregarIngrediente(chunks, 200.0);
        galletaChocolate.agregarIngrediente(polvo, 5.0);
        galletaChocolate.agregarIngrediente(bicarbonato, 2.0);
        galletaChocolate.agregarIngrediente(sal, 2.0);
        galletaChocolate.agregarIngrediente(vainilla, 5.0);
        lista.add(galletaChocolate);

        // 3. Galleta Red Velvet
        Receta galletaRedVelvet = new Receta();
        galletaRedVelvet.setNombre("Galleta Red Velvet");
        galletaRedVelvet.setRendimiento(35);
        galletaRedVelvet.agregarIngrediente(harinaTrigo, 450.0);
        galletaRedVelvet.agregarIngrediente(azucarBlanca, 200.0);
        galletaRedVelvet.agregarIngrediente(mantequilla, 250.0);
        galletaRedVelvet.agregarIngrediente(huevo, 2.0);
        galletaRedVelvet.agregarIngrediente(cocoa, 20.0);
        galletaRedVelvet.agregarIngrediente(coloranteRojo, 10.0);
        galletaRedVelvet.agregarIngrediente(vainilla, 5.0);
        galletaRedVelvet.agregarIngrediente(polvo, 5.0);
        galletaRedVelvet.agregarIngrediente(sal, 2.0);
        lista.add(galletaRedVelvet);

        // 4. Galleta de coco y arequipe (sin arequipe, pero con coco y leche condensada en la receta)
        Receta galletaCoco = new Receta();
        galletaCoco.setNombre("Galleta de coco");
        galletaCoco.setRendimiento(35);
        galletaCoco.agregarIngrediente(harinaTrigo, 350.0);
        galletaCoco.agregarIngrediente(azucarBlanca, 150.0);
        galletaCoco.agregarIngrediente(mantequilla, 200.0);
        galletaCoco.agregarIngrediente(huevo, 2.0);
        galletaCoco.agregarIngrediente(coco, 150.0);
        galletaCoco.agregarIngrediente(lecheCondensada, 100.0);
        galletaCoco.agregarIngrediente(vainilla, 5.0);
        galletaCoco.agregarIngrediente(polvo, 5.0);
        galletaCoco.agregarIngrediente(sal, 1.0);
        lista.add(galletaCoco);

        // 5. Brownie
        Receta brownie = new Receta();
        brownie.setNombre("Brownie");
        brownie.setRendimiento(20);
        brownie.agregarIngrediente(harinaTrigo, 300.0);
        brownie.agregarIngrediente(azucarBlanca, 400.0);
        brownie.agregarIngrediente(mantequilla, 300.0);
        brownie.agregarIngrediente(huevo, 4.0);
        brownie.agregarIngrediente(cocoa, 100.0);
        brownie.agregarIngrediente(chunks, 200.0);
        brownie.agregarIngrediente(vainilla, 5.0);
        brownie.agregarIngrediente(sal, 2.0);
        brownie.agregarIngrediente(polvo, 5.0);
        lista.add(brownie);

        // 6. Brownie con nueces
        Receta brownieNueces = new Receta();
        brownieNueces.setNombre("Brownie con nueces");
        brownieNueces.setRendimiento(20);
        brownieNueces.agregarIngrediente(harinaTrigo, 300.0);
        brownieNueces.agregarIngrediente(azucarMorena, 400.0);
        brownieNueces.agregarIngrediente(mantequilla, 300.0);
        brownieNueces.agregarIngrediente(huevo, 4.0);
        brownieNueces.agregarIngrediente(cocoa, 100.0);
        brownieNueces.agregarIngrediente(nueces, 150.0);
        brownieNueces.agregarIngrediente(vainilla, 5.0);
        brownieNueces.agregarIngrediente(sal, 2.0);
        brownieNueces.agregarIngrediente(polvo, 5.0);
        lista.add(brownieNueces);

        // 7. Torta de zanahoria (versión sencilla)
        Receta tortaZanahoria = new Receta();
        tortaZanahoria.setNombre("Torta de zanahoria");
        tortaZanahoria.setRendimiento(12);
        tortaZanahoria.agregarIngrediente(harinaTrigo, 400.0);
        tortaZanahoria.agregarIngrediente(azucarMorena, 300.0);
        tortaZanahoria.agregarIngrediente(azucarPulverizada, 200.0);
        tortaZanahoria.agregarIngrediente(huevo, 3.0);
        tortaZanahoria.agregarIngrediente(zanahoria, 300.0); // asumo que existe, si no, debo agregarla antes
        tortaZanahoria.agregarIngrediente(vainilla, 5.0);
        tortaZanahoria.agregarIngrediente(polvo, 5.0);
        tortaZanahoria.agregarIngrediente(bicarbonato, 2.0);
        tortaZanahoria.agregarIngrediente(sal, 2.0);
        // Agregar nueces
        tortaZanahoria.agregarIngrediente(nueces, 100.0);
        lista.add(tortaZanahoria);

        // 8. Torta de frutos rojos
        Receta tortaFrutosRojos = new Receta();
        tortaFrutosRojos.setNombre("Torta de frutos rojos");
        tortaFrutosRojos.setRendimiento(12);
        tortaFrutosRojos.agregarIngrediente(harinaTrigo, 400.0);
        tortaFrutosRojos.agregarIngrediente(azucarBlanca, 300.0);
        tortaFrutosRojos.agregarIngrediente(mantequilla, 200.0);
        tortaFrutosRojos.agregarIngrediente(huevo, 3.0);
        tortaFrutosRojos.agregarIngrediente(leche, 200.0);
        tortaFrutosRojos.agregarIngrediente(mermelada, 150.0);
        tortaFrutosRojos.agregarIngrediente(fresas, 100.0);
        tortaFrutosRojos.agregarIngrediente(arandanos, 100.0);
        tortaFrutosRojos.agregarIngrediente(vainilla, 5.0);
        tortaFrutosRojos.agregarIngrediente(polvo, 5.0);
        tortaFrutosRojos.agregarIngrediente(sal, 2.0);
        lista.add(tortaFrutosRojos);

        // 9. Torta de chocolate
        Receta tortaChocolate = new Receta();
        tortaChocolate.setNombre("Torta de chocolate");
        tortaChocolate.setRendimiento(12);
        tortaChocolate.agregarIngrediente(harinaTrigo, 350.0);
        tortaChocolate.agregarIngrediente(azucarBlanca, 350.0);
        tortaChocolate.agregarIngrediente(mantequilla, 200.0);
        tortaChocolate.agregarIngrediente(huevo, 3.0);
        tortaChocolate.agregarIngrediente(cocoa, 120.0);
        tortaChocolate.agregarIngrediente(leche, 200.0);
        tortaChocolate.agregarIngrediente(chocolateNegro, 100.0);
        tortaChocolate.agregarIngrediente(vainilla, 5.0);
        tortaChocolate.agregarIngrediente(polvo, 5.0);
        tortaChocolate.agregarIngrediente(bicarbonato, 2.0);
        tortaChocolate.agregarIngrediente(sal, 2.0);
        lista.add(tortaChocolate);

        // 10. Torta de café
        Receta tortaCafe = new Receta();
        tortaCafe.setNombre("Torta de café");
        tortaCafe.setRendimiento(12);
        tortaCafe.agregarIngrediente(harinaTrigo, 400.0);
        tortaCafe.agregarIngrediente(azucarMorena, 300.0);
        tortaCafe.agregarIngrediente(mantequilla, 200.0);
        tortaCafe.agregarIngrediente(huevo, 3.0);
        tortaCafe.agregarIngrediente(leche, 200.0);
        tortaCafe.agregarIngrediente(cafeInstantaneo, 30.0);
        tortaCafe.agregarIngrediente(vainilla, 5.0);
        tortaCafe.agregarIngrediente(polvo, 5.0);
        tortaCafe.agregarIngrediente(sal, 2.0);
        // Agregar nueces
        tortaCafe.agregarIngrediente(nueces, 100.0);
        lista.add(tortaCafe);

        recetas = recetaRepo.saveAll(lista);
        System.out.println("✅ " + recetas.size() + " recetas cargadas.");
    }

    // ============================================================
    // 4. PRODUCTOS (simples y combos)
    // ============================================================
    private void cargarProductos() {
        if (productoRepo.count() > 0) {
            System.out.println("ℹ️ Productos ya existen, omitiendo carga.");
            productos = productoRepo.findAll();
            return;
        }

        Receta galletaBase = getReceta("Galleta base");
        Receta galletaChocolate = getReceta("Galleta de chocolate");
        Receta galletaRedVelvet = getReceta("Galleta Red Velvet");
        Receta galletaCoco = getReceta("Galleta de coco");
        Receta brownie = getReceta("Brownie");
        Receta brownieNueces = getReceta("Brownie con nueces");
        Receta tortaZanahoria = getReceta("Torta de zanahoria");
        Receta tortaFrutosRojos = getReceta("Torta de frutos rojos");
        Receta tortaChocolate = getReceta("Torta de chocolate");
        Receta tortaCafe = getReceta("Torta de café");

        List<Producto> lista = new ArrayList<>();

        // Productos simples
        Producto p1 = new Producto();
        p1.setNombre("Galleta Vainilla");
        p1.setDescripcion("Galleta suave con esencia de vainilla");
        p1.setPrecioVenta(2500.0);
        p1.setTipo(TipoProducto.SIMPLE);
        p1.setReceta(galletaBase);
        p1.setActivo(true);
        lista.add(p1);

        Producto p2 = new Producto();
        p2.setNombre("Galleta Chocolate");
        p2.setDescripcion("Galleta con chocolate y chunks");
        p2.setPrecioVenta(3000.0);
        p2.setTipo(TipoProducto.SIMPLE);
        p2.setReceta(galletaChocolate);
        p2.setActivo(true);
        lista.add(p2);

        Producto p3 = new Producto();
        p3.setNombre("Galleta Red Velvet");
        p3.setDescripcion("Galleta roja con un toque de cocoa");
        p3.setPrecioVenta(3500.0);
        p3.setTipo(TipoProducto.SIMPLE);
        p3.setReceta(galletaRedVelvet);
        p3.setActivo(true);
        lista.add(p3);

        Producto p4 = new Producto();
        p4.setNombre("Galleta Coco");
        p4.setDescripcion("Galleta con coco y leche condensada");
        p4.setPrecioVenta(3200.0);
        p4.setTipo(TipoProducto.SIMPLE);
        p4.setReceta(galletaCoco);
        p4.setActivo(true);
        lista.add(p4);

        Producto p5 = new Producto();
        p5.setNombre("Brownie Clásico");
        p5.setDescripcion("Brownie húmedo con chunks de chocolate");
        p5.setPrecioVenta(4000.0);
        p5.setTipo(TipoProducto.SIMPLE);
        p5.setReceta(brownie);
        p5.setActivo(true);
        lista.add(p5);

        Producto p6 = new Producto();
        p6.setNombre("Brownie con Nueces");
        p6.setDescripcion("Brownie con nueces y chocolate");
        p6.setPrecioVenta(4500.0);
        p6.setTipo(TipoProducto.SIMPLE);
        p6.setReceta(brownieNueces);
        p6.setActivo(true);
        lista.add(p6);

        Producto p7 = new Producto();
        p7.setNombre("Torta de Zanahoria (porción)");
        p7.setDescripcion("Porción de torta de zanahoria con nueces");
        p7.setPrecioVenta(6000.0);
        p7.setTipo(TipoProducto.SIMPLE);
        p7.setReceta(tortaZanahoria);
        p7.setActivo(true);
        lista.add(p7);

        Producto p8 = new Producto();
        p8.setNombre("Torta de Frutos Rojos (porción)");
        p8.setDescripcion("Porción de torta con fresas y arándanos");
        p8.setPrecioVenta(6500.0);
        p8.setTipo(TipoProducto.SIMPLE);
        p8.setReceta(tortaFrutosRojos);
        p8.setActivo(true);
        lista.add(p8);

        Producto p9 = new Producto();
        p9.setNombre("Torta de Chocolate (porción)");
        p9.setDescripcion("Porción de torta de chocolate intenso");
        p9.setPrecioVenta(7000.0);
        p9.setTipo(TipoProducto.SIMPLE);
        p9.setReceta(tortaChocolate);
        p9.setActivo(true);
        lista.add(p9);

        Producto p10 = new Producto();
        p10.setNombre("Torta de Café (porción)");
        p10.setDescripcion("Porción de torta de café con nueces");
        p10.setPrecioVenta(6800.0);
        p10.setTipo(TipoProducto.SIMPLE);
        p10.setReceta(tortaCafe);
        p10.setActivo(true);
        lista.add(p10);

        // Productos combos
        Producto combo1 = new Producto();
        combo1.setNombre("Combo Galletas (x6)");
        combo1.setDescripcion("6 galletas variadas (2 vainilla, 2 chocolate, 2 red velvet)");
        combo1.setPrecioVenta(15000.0);
        combo1.setTipo(TipoProducto.COMBO);
        combo1.setProductosHijos(Arrays.asList(p1, p1, p2, p2, p3, p3));
        combo1.setActivo(true);
        lista.add(combo1);

        Producto combo2 = new Producto();
        combo2.setNombre("Combo Brownie + Café");
        combo2.setDescripcion("Brownie clásico + una bebida de café (simulado)");
        combo2.setPrecioVenta(12000.0);
        combo2.setTipo(TipoProducto.COMBO);
        combo2.setProductosHijos(Arrays.asList(p5, p10)); // Brownie + porción de torta de café (simula bebida)
        combo2.setActivo(true);
        lista.add(combo2);

        Producto combo3 = new Producto();
        combo3.setNombre("Combo Dulce (2 Brownies + 1 Galleta)");
        combo3.setDescripcion("2 brownies clásicos y 1 galleta de chocolate");
        combo3.setPrecioVenta(10000.0);
        combo3.setTipo(TipoProducto.COMBO);
        combo3.setProductosHijos(Arrays.asList(p5, p5, p2));
        combo3.setActivo(true);
        lista.add(combo3);

        productos = productoRepo.saveAll(lista);
        System.out.println("✅ " + productos.size() + " productos cargados.");
    }

    // ============================================================
    // 5. VENTAS DEL MES (distribuidas en julio 2026)
    // ============================================================
    private void cargarVentasDelMes() {
        if (ventaRepo.count() > 0) {
            System.out.println("ℹ️ Ventas ya existen, omitiendo carga.");
            return;
        }

        // Fecha actual: 29 de julio 2026 (asumimos)
        int year = 2026;
        int month = 7;
        int daysInMonth = 31;

        // Crear una lista de productos para usar en las ventas
        List<Producto> prods = productoRepo.findAll();

        if (prods.isEmpty()) {
            System.out.println("⚠️ No hay productos para crear ventas.");
            return;
        }

        List<Venta> ventas = new ArrayList<>();

        // Generar ventas para cada día del mes (con intensidad variable)
        for (int day = 1; day <= daysInMonth; day++) {
            // Días con más ventas: fines de semana (sábado y domingo) y días específicos
            LocalDate fecha = LocalDate.of(year, month, day);
            int dayOfWeek = fecha.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
            int numVentas;

            if (dayOfWeek >= 6) { // Sábado o domingo
                numVentas = random.nextInt(8) + 8; // 8 a 15 ventas
            } else if (day == 15 || day == 20 || day == 25) { // Días especiales (quincena, etc.)
                numVentas = random.nextInt(10) + 10; // 10 a 20 ventas
            } else {
                numVentas = random.nextInt(6) + 3; // 3 a 8 ventas
            }

            for (int i = 0; i < numVentas; i++) {
                Venta venta = crearVentaAleatoria(fecha, prods);
                ventas.add(venta);
            }
        }

        // Guardar todas las ventas (en lotes de 100 para rendimiento)
        int batchSize = 100;
        for (int i = 0; i < ventas.size(); i += batchSize) {
            int end = Math.min(i + batchSize, ventas.size());
            ventaRepo.saveAll(ventas.subList(i, end));
        }

        System.out.println("✅ " + ventas.size() + " ventas cargadas para todo el mes.");
    }

    private Venta crearVentaAleatoria(LocalDate fecha, List<Producto> productos) {
        // Seleccionar cliente aleatorio
        String cliente = clientes.get(random.nextInt(clientes.size()));
        String metodoPago = metodosPago.get(random.nextInt(metodosPago.size()));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setMetodoPago(metodoPago);
        venta.setFecha(LocalDateTime.of(fecha, LocalTime.of(random.nextInt(10) + 8, random.nextInt(60))));
        venta.setEstado(EstadoVenta.PAGADO);

        // Número de productos en la venta (1 a 5)
        int numItems = random.nextInt(5) + 1;
        double total = 0.0;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            Producto prod = productos.get(random.nextInt(productos.size()));
            int cantidad = random.nextInt(3) + 1; // 1 a 3 unidades
            double precioUnitario = prod.getPrecioVenta();
            double subtotal = precioUnitario * cantidad;

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(prod);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);
            total += subtotal;
        }

        // Aplicar comisión si es Rappi
        if (metodoPago.equals("RAPPI")) {
            double comision = total * 0.10;
            total = total - comision;
        }

        venta.setTotal(total);
        venta.setDetalles(detalles);
        return venta;
    }

    // ============================================================
    // MÉTODOS AUXILIARES PARA OBTENER INGREDIENTES Y RECETAS
    // ============================================================
    private Ingrediente getIngrediente(String nombre) {
        return ingredienteRepo.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado: " + nombre));
    }

    private Receta getReceta(String nombre) {
        return recetaRepo.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada: " + nombre));
    }
}