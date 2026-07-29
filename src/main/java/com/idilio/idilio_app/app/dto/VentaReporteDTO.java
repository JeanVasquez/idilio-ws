package com.idilio.idilio_app.app.dto;

import lombok.Data;

@Data
public class VentaReporteDTO {
    private String fecha;
    private Double total;
    private Integer cantidadVentas;
    private String metodoPago;
}

