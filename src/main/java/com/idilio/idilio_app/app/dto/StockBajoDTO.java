package com.idilio.idilio_app.app.dto;

import lombok.Data;

@Data
public class StockBajoDTO {
    private String ingredienteNombre;
    private Double stockActual;
    private Double stockMinimo;
}
