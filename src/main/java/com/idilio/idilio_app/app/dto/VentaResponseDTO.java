package com.idilio.idilio_app.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private String cliente;
    private String metodoPago;
    private Double total;
    private String estado;
    private List<DetalleVentaResponseDTO> detalles;
}

