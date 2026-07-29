package com.idilio.idilio_app.app.repository;

import com.idilio.idilio_app.app.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findByNombre(String nombre);
}