package com.idilio.idilio_app.app.repository;

import com.idilio.idilio_app.app.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findAllByOrderByFechaDesc();

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}