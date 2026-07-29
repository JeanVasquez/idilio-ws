package com.idilio.idilio_app.app.service;

import com.idilio.idilio_app.app.model.ParametroGlobal;
import com.idilio.idilio_app.app.repository.ParametroGlobalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParametroGlobalService {

    private final ParametroGlobalRepository parametroRepo;

    public ParametroGlobalService(ParametroGlobalRepository parametroRepo) {
        this.parametroRepo = parametroRepo;
    }

    public Double obtenerValorDouble(String clave, Double defaultValue) {
        return parametroRepo.findByClave(clave)
                .map(p -> Double.parseDouble(p.getValor()))
                .orElse(defaultValue);
    }

    public ParametroGlobal actualizar(String clave, String valor) {
        ParametroGlobal parametro = parametroRepo.findByClave(clave)
                .orElse(new ParametroGlobal());
        parametro.setClave(clave);
        parametro.setValor(valor);
        return parametroRepo.save(parametro);
    }

    public ParametroGlobal crear(ParametroGlobal parametro) {
        return parametroRepo.save(parametro);
    }

    public void eliminar(Long id) {
        parametroRepo.deleteById(id);
    }

    public ParametroGlobal obtenerPorClave(String clave) {
        return parametroRepo.findByClave(clave)
                .orElseThrow(() -> new RuntimeException("Parametro no encontrado"));
    }

    public List<ParametroGlobal> listarTodos() {
        return parametroRepo.findAll();
    }
}