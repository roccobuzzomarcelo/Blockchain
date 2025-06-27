package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.BlockManagerService;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.ResultadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mining")
public class MiningResultController {


    @Autowired
    RedisTemplate<String,CandidateBlock> redisTemplate;

    @Autowired
    private ResultadoService resultadoService;

    @Autowired
    BlockManagerService blockManagerService;

    @PostMapping("/solved_task")
    public ResponseEntity<String> recibirSolucion(@RequestBody MiningResultDTO resultado) {
        boolean valido = resultadoService.procesarResultado(resultado);
        if (valido) {

            return ResponseEntity.ok("Solución válida recibida.");
        } else {
            return ResponseEntity.badRequest().body("Solución inválida.");
        }
    }


    // no agarra bien el valor del estado del candidato
    @GetMapping("/block-status/{blockId}")
    public ResponseEntity<String> getBlockStatus(@PathVariable String blockId) {
        CandidateBlock candidato = blockManagerService.obtenerCandidateBlock(blockId);

        if (candidato == null) {
            System.out.println("No exite");
            return ResponseEntity.ok("NO_EXISTE");
        }

        if (candidato.getEstado() == null) {
            // Setear un estado por defecto para evitar NPE
            candidato.setEstado(EstadoBlock.PENDIENTE);
            System.out.println("es candidato es null");
        }

        System.out.println("lo consiguio");
        return ResponseEntity.ok(candidato.getEstado().name());
    }


}
