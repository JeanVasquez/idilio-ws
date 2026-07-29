package com.idilio.idilio_app.app.service;

import com.idilio.idilio_app.app.model.Ingrediente;
import com.idilio.idilio_app.app.repository.IngredienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    public Ingrediente guardar(Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    public Ingrediente buscarPorId(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));
    }

    public void eliminar(Long id) {
        ingredienteRepository.deleteById(id);
    }

    public void ajustarStock(Long id, Double nuevoStock) {
        Ingrediente ing = buscarPorId(id);
        ing.setStock(nuevoStock);
        ingredienteRepository.save(ing);
    }

    public void descontarStock(Ingrediente ing, Double cantidad) {
        if (ing.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente de " + ing.getNombre());
        }
        ing.setStock(ing.getStock() - cantidad);
        ingredienteRepository.save(ing);
    }

    public List<Ingrediente> listarStockBajo() {
        return ingredienteRepository.findAll().stream()
                .filter(ing -> ing.getStock() < ing.getStockMinimo())
                .toList();
    }
}