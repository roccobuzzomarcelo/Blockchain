package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mining")
public class MiningResultController {

    @Autowired
    private ResultadoService resultadoService;

    @PostMapping("/solved_task")
    public ResponseEntity<String> recibirSolucion(@RequestBody MiningResultDTO resultado) {
        boolean valido = resultadoService.procesarResultado(resultado);
        if (valido) {

            return ResponseEntity.ok("Solución válida recibida.");
        } else {
            return ResponseEntity.badRequest().body("Solución inválida.");
        }
    }
}
