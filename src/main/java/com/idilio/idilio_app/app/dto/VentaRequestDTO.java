package com.idilio.idilio_app.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequestDTO {
    private String cliente;
    private String metodoPago; // EFECTIVO, NEQUI, BANCOLOMBIA, RAPPI, ...
    private List<DetalleVentaDTO> detalles;
}