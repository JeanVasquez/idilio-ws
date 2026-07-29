package com.idilio.idilio_app.app.service;


import com.idilio.idilio_app.app.dto.IngredienteRecetaDTO;
import com.idilio.idilio_app.app.dto.RecetaDTO;
import com.idilio.idilio_app.app.model.Ingrediente;
import com.idilio.idilio_app.app.model.IngredienteReceta;
import com.idilio.idilio_app.app.model.Receta;
import com.idilio.idilio_app.app.repository.IngredienteRepository;
import com.idilio.idilio_app.app.repository.RecetaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecetaService {

    private final RecetaRepository recetaRepo;

    private final IngredienteRepository ingredienteRepo;

    public RecetaService(RecetaRepository recetaRepo, IngredienteRepository ingredienteRepo) {
        this.recetaRepo = recetaRepo;
        this.ingredienteRepo = ingredienteRepo;
    }

    public List<Receta> listarTodas() {
        return recetaRepo.findAll();
    }

    public Receta buscarPorId(Long id) {
        return recetaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
    }

    public Receta crear(RecetaDTO dto) {
        Receta receta = new Receta();
        receta.setNombre(dto.getNombre());
        receta.setRendimiento(dto.getRendimiento());

        return getReceta(dto, receta);
    }

    @NonNull
    private Receta getReceta(RecetaDTO dto, Receta receta) {
        for (IngredienteRecetaDTO irDTO : dto.getIngredientes()) {
            Ingrediente ingrediente = ingredienteRepo.findById(irDTO.getIngredienteId())
                    .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

            IngredienteReceta ir = new IngredienteReceta();
            ir.setReceta(receta);
            ir.setIngrediente(ingrediente);
            ir.setCantidad(irDTO.getCantidad());
            receta.getIngredientes().add(ir);
        }

        return recetaRepo.save(receta);
    }

    public Receta actualizar(Long id, RecetaDTO dto) {
        Receta receta = buscarPorId(id);
        receta.setNombre(dto.getNombre());
        receta.setRendimiento(dto.getRendimiento());

        // Limpiar ingredientes existentes y agregar los nuevos
        receta.getIngredientes().clear();

        return getReceta(dto, receta);
    }

    public Double calcularCostoUnitario(Long recetaId) {
        Receta receta = buscarPorId(recetaId);
        double costoTotal = receta.getIngredientes().stream()
                .mapToDouble(ir -> ir.getCantidad() * ir.getIngrediente().getPrecioCompra())
                .sum();
        return costoTotal / receta.getRendimiento();
    }

    public void eliminar(Long id) {
        recetaRepo.deleteById(id);
    }
}