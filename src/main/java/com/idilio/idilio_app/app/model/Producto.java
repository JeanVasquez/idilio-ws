package com.idilio.idilio_app.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private Double precioVenta;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipo;

    @OneToOne
    private Receta receta; // si es SIMPLE

    @ManyToMany
    @ToString.Exclude
    private List<Producto> productosHijos = new ArrayList<>(); // si es COMBO

    private Boolean activo = true;
}