package com.idilio.idilio_app.app.repository;

import com.idilio.idilio_app.app.model.ParametroGlobal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroGlobalRepository extends JpaRepository<ParametroGlobal, Long> {
    Optional<ParametroGlobal> findByClave(String clave);
}