package com.idilio.idilio_app.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String unidad; // ej. "gr", "ml", "unidad"

    private Double stock = 0.0;

    private Double stockMinimo = 0.0;

    private Double precioCompra = 0.0; // costo por unidad (ej. por gramo)
}