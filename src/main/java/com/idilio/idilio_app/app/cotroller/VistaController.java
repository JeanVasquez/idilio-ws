package com.idilio.idilio_app.app.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    @GetMapping("/ingredientes")
    public String vistaInventario() {
        return "inventario"; // busca templates/ingredientes.html
    }
}