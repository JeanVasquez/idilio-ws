package com.idilio.idilio_app.app.dto;

import lombok.Data;

@Data
public class ProductoVendidoDTO {
    private String productoNombre;
    private Long cantidadVendida;
    private Double totalVendido;
}
