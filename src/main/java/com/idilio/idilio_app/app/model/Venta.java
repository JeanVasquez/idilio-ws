package com.idilio.idilio_app.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha = LocalDateTime.now();

    private String cliente;

    private String metodoPago; // EFECTIVO, NEQUI, BANCOLOMBIA, RAPPI, EMILLY, GISELE

    private Double total;

    @Enumerated(EnumType.STRING)
    private EstadoVenta estado = EstadoVenta.PAGADO;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<DetalleVenta> detalles = new ArrayList<>();
}