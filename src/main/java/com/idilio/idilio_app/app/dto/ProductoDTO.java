package com.idilio.idilio_app.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private Double precioVenta;
    private String tipo; // "SIMPLE" o "COMBO"
    private Long recetaId; // Solo si es SIMPLE
    private List<Long> productosHijosIds; // Solo si es COMBO
    private Boolean activo;
}