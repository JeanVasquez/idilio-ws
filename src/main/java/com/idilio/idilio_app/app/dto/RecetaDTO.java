package com.idilio.idilio_app.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecetaDTO {
    private String nombre;
    private Integer rendimiento;
    private List<IngredienteRecetaDTO> ingredientes;
}