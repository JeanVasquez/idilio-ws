package com.idilio.idilio_app.app.service;

import com.idilio.idilio_app.app.dto.ProductoDTO;
import com.idilio.idilio_app.app.model.Producto;
import com.idilio.idilio_app.app.model.Receta;
import com.idilio.idilio_app.app.model.TipoProducto;
import com.idilio.idilio_app.app.repository.ProductoRepository;
import com.idilio.idilio_app.app.repository.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepo;

    private final RecetaRepository recetaRepo;

    private final ParametroGlobalService parametroService; // Lo crearemos después

    public ProductoService(ProductoRepository productoRepo, RecetaRepository recetaRepo, ParametroGlobalService parametroService) {
        this.productoRepo = productoRepo;
        this.recetaRepo = recetaRepo;
        this.parametroService = parametroService;
    }

    public List<Producto> listarTodos(Boolean soloActivos) {
        if (soloActivos != null && soloActivos) {
            return productoRepo.findByActivoTrue();
        }
        return productoRepo.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto crear(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setActivo(dto.getActivo() == null || dto.getActivo());

        configurarTipoProducto(producto, dto);
        return productoRepo.save(producto);
    }

    public Producto actualizar(Long id, ProductoDTO dto) {
        Producto producto = buscarPorId(id);
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : producto.getActivo());

        // Limpiar relaciones anteriores
        producto.setReceta(null);
        producto.setProductosHijos(new ArrayList<>());

        configurarTipoProducto(producto, dto);
        return productoRepo.save(producto);
    }

    private void configurarTipoProducto(Producto producto, ProductoDTO dto) {
        if (dto.getTipo().equalsIgnoreCase("SIMPLE")) {
            producto.setTipo(TipoProducto.SIMPLE);
            if (dto.getRecetaId() == null) {
                throw new RuntimeException("Un producto SIMPLE debe tener una receta asociada");
            }
            Receta receta = recetaRepo.findById(dto.getRecetaId())
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
            producto.setReceta(receta);
            producto.setProductosHijos(null);

        } else if (dto.getTipo().equalsIgnoreCase("COMBO")) {
            producto.setTipo(TipoProducto.COMBO);
            if (dto.getProductosHijosIds() == null || dto.getProductosHijosIds().isEmpty()) {
                throw new RuntimeException("Un producto COMBO debe tener al menos un producto hijo");
            }
            List<Producto> hijos = new ArrayList<>();
            for (Long hijoId : dto.getProductosHijosIds()) {
                Producto hijo = buscarPorId(hijoId);
                if (hijo.getTipo() == TipoProducto.COMBO) {
                    throw new RuntimeException("No se permiten combos anidados. El producto " + hijo.getNombre() + " es un combo.");
                }
                hijos.add(hijo);
            }
            producto.setProductosHijos(hijos);
            producto.setReceta(null);

        } else {
            throw new RuntimeException("Tipo de producto no válido. Debe ser SIMPLE o COMBO");
        }
    }

    public Double calcularPrecioSugerido(Long productoId) {
        Producto producto = buscarPorId(productoId);
        Double costoUnitario = calcularCostoUnitario(producto);
        Double porcentajeCosto = parametroService.obtenerValorDouble("porcentaje_costo", 35.0);
        Double factorRedondeo = parametroService.obtenerValorDouble("factor_redondeo", 500.0);

        Double precio = costoUnitario * (100.0 / porcentajeCosto);
        // Redondear al múltiplo de factorRedondeo
        return Math.round(precio / factorRedondeo) * factorRedondeo;
    }

    public Double calcularCostoUnitario(Producto producto) {
        if (producto.getTipo() == TipoProducto.SIMPLE) {
            if (producto.getReceta() == null) {
                throw new RuntimeException("El producto SIMPLE no tiene receta asociada");
            }
            Receta receta = producto.getReceta();
            double costoTotal = receta.getIngredientes().stream()
                    .mapToDouble(ir -> ir.getCantidad() * ir.getIngrediente().getPrecioCompra())
                    .sum();
            return costoTotal / receta.getRendimiento();

        } else if (producto.getTipo() == TipoProducto.COMBO) {
            if (producto.getProductosHijos() == null || producto.getProductosHijos().isEmpty()) {
                throw new RuntimeException("El producto COMBO no tiene productos hijos");
            }
            return producto.getProductosHijos().stream()
                    .mapToDouble(this::calcularCostoUnitario)
                    .sum();

        } else {
            throw new RuntimeException("Tipo de producto no reconocido");
        }
    }

    public Producto cambiarEstado(Long id, Boolean activo) {
        Producto producto = buscarPorId(id);
        producto.setActivo(activo);
        return productoRepo.save(producto);
    }

    public void eliminar(Long id) {
        productoRepo.deleteById(id);
    }
}