package com.idilio.idilio_app.app.service;

import com.idilio.idilio_app.app.dto.DetalleVentaDTO;
import com.idilio.idilio_app.app.dto.DetalleVentaResponseDTO;
import com.idilio.idilio_app.app.dto.VentaRequestDTO;
import com.idilio.idilio_app.app.dto.VentaResponseDTO;
import com.idilio.idilio_app.app.model.*;
import com.idilio.idilio_app.app.repository.IngredienteRepository;
import com.idilio.idilio_app.app.repository.ProductoRepository;
import com.idilio.idilio_app.app.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepo;

    private final ProductoRepository productoRepo;

    private final IngredienteRepository ingredienteRepo;

    private final ParametroGlobalService parametroService;

    public VentaService(VentaRepository ventaRepo, ProductoRepository productoRepo, IngredienteRepository ingredienteRepo, ParametroGlobalService parametroService) {
        this.ventaRepo = ventaRepo;
        this.productoRepo = productoRepo;
        this.ingredienteRepo = ingredienteRepo;
        this.parametroService = parametroService;
    }

    // Registrar una nueva venta
    public VentaResponseDTO registrarVenta(VentaRequestDTO request) {
        // 1. Validar que los productos existan y estén activos
        for (DetalleVentaDTO detalleDTO : request.getDetalles()) {
            Producto producto = productoRepo.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDTO.getProductoId()));
            if (!producto.getActivo()) {
                throw new RuntimeException("El producto " + producto.getNombre() + " no está activo");
            }
        }

        // 2. Crear la venta
        Venta venta = new Venta();
        venta.setCliente(request.getCliente());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.PAGADO);

        List<DetalleVenta> detalles = new ArrayList<>();
        double totalBruto = 0.0;

        // 3. Procesar cada detalle
        for (DetalleVentaDTO detalleDTO : request.getDetalles()) {
            Producto producto = productoRepo.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDTO.getProductoId()));

            // Descontar stock según el tipo de producto
            descontarStockProducto(producto, detalleDTO.getCantidad());

            // Crear el detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setSubtotal(producto.getPrecioVenta() * detalleDTO.getCantidad());

            detalles.add(detalle);
            totalBruto += detalle.getSubtotal();
        }

        venta.setDetalles(detalles);

        // 4. Aplicar comisión si es Rappi
        double comision = 0.0;
        if ("RAPPI".equalsIgnoreCase(request.getMetodoPago())) {
            Double comisionPorcentaje = parametroService.obtenerValorDouble("comision_rappi_porcentaje", 10.0);
            comision = totalBruto * (comisionPorcentaje / 100.0);
        }

        double totalNeto = totalBruto - comision;
        venta.setTotal(totalNeto);

        // 5. Guardar la venta
        venta = ventaRepo.save(venta);

        // 6. Construir y devolver la respuesta
        return convertirAResponse(venta);
    }

    // Descontar stock de un producto (simple o combo)
    private void descontarStockProducto(Producto producto, Integer cantidad) {
        if (producto.getTipo() == TipoProducto.SIMPLE) {
            descontarReceta(producto.getReceta(), cantidad);
        } else if (producto.getTipo() == TipoProducto.COMBO) {
            for (Producto hijo : producto.getProductosHijos()) {
                if (hijo.getTipo() != TipoProducto.SIMPLE) {
                    throw new RuntimeException("No se permiten combos anidados: " + hijo.getNombre());
                }
                descontarReceta(hijo.getReceta(), cantidad);
            }
        } else {
            throw new RuntimeException("Tipo de producto no válido: " + producto.getTipo());
        }
    }

    // Descontar ingredientes de una receta
    private void descontarReceta(Receta receta, Integer cantidad) {
        if (receta == null) {
            throw new RuntimeException("El producto no tiene receta definida");
        }

        for (IngredienteReceta ir : receta.getIngredientes()) {
            Ingrediente ingrediente = ir.getIngrediente();
            Double cantidadNecesaria = ir.getCantidad() * cantidad;

            if (ingrediente.getStock() < cantidadNecesaria) {
                throw new RuntimeException("Stock insuficiente de " + ingrediente.getNombre() +
                        ". Disponible: " + ingrediente.getStock() + ", necesario: " + cantidadNecesaria);
            }

            ingrediente.setStock(ingrediente.getStock() - cantidadNecesaria);
            ingredienteRepo.save(ingrediente);
        }
    }

    // Cancelar una venta (reponer stock)
    public VentaResponseDTO cancelarVenta(Long ventaId) {
        Venta venta = ventaRepo.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() == EstadoVenta.CANCELADO) {
            throw new RuntimeException("La venta ya está cancelada");
        }

        // Reponer stock de todos los productos de la venta
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getTipo() == TipoProducto.SIMPLE) {
                reponerReceta(producto.getReceta(), detalle.getCantidad());
            } else if (producto.getTipo() == TipoProducto.COMBO) {
                for (Producto hijo : producto.getProductosHijos()) {
                    reponerReceta(hijo.getReceta(), detalle.getCantidad());
                }
            }
        }

        venta.setEstado(EstadoVenta.CANCELADO);
        venta = ventaRepo.save(venta);

        return convertirAResponse(venta);
    }

    // Reponer ingredientes de una receta (al cancelar)
    private void reponerReceta(Receta receta, Integer cantidad) {
        for (IngredienteReceta ir : receta.getIngredientes()) {
            Ingrediente ingrediente = ir.getIngrediente();
            Double cantidadReponer = ir.getCantidad() * cantidad;
            ingrediente.setStock(ingrediente.getStock() + cantidadReponer);
            ingredienteRepo.save(ingrediente);
        }
    }

    // Listar todas las ventas
    public List<VentaResponseDTO> listarTodas() {
        return ventaRepo.findAllByOrderByFechaDesc()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Obtener una venta por ID
    public VentaResponseDTO obtenerPorId(Long id) {
        Venta venta = ventaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return convertirAResponse(venta);
    }

    // Méthod auxiliar para convertir Venta a VentaResponseDTO
    private VentaResponseDTO convertirAResponse(Venta venta) {
        VentaResponseDTO response = new VentaResponseDTO();
        response.setId(venta.getId());
        response.setFecha(venta.getFecha());
        response.setCliente(venta.getCliente());
        response.setMetodoPago(venta.getMetodoPago());
        response.setTotal(venta.getTotal());
        response.setEstado(venta.getEstado().name());

        List<DetalleVentaResponseDTO> detallesResponse = venta.getDetalles().stream()
                .map(detalle -> {
                    DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
                    dto.setProductoId(detalle.getProducto().getId());
                    dto.setProductoNombre(detalle.getProducto().getNombre());
                    dto.setCantidad(detalle.getCantidad());
                    dto.setPrecioUnitario(detalle.getPrecioUnitario());
                    dto.setSubtotal(detalle.getSubtotal());
                    return dto;
                })
                .collect(Collectors.toList());

        response.setDetalles(detallesResponse);
        return response;
    }
}