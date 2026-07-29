package com.idilio.idilio_app.app.repository;

import com.idilio.idilio_app.app.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    Optional<Receta> findByNombre(String nombre);
}