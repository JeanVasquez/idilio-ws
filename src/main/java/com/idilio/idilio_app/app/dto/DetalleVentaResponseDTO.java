package com.idilio.idilio_app.app.dto;

import lombok.Data;

@Data
public class DetalleVentaResponseDTO {
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
