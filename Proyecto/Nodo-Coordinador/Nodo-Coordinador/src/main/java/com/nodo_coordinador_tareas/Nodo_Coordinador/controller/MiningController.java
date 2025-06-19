package com.nodo_coordinador_tareas.Nodo_Coordinador.controller;



import com.nodo_coordinador_tareas.Nodo_Coordinador.config.MiningTaskPublisher;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningTask;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.TransactionPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/mining")
public class MiningController {

    @Autowired
    private TransactionPoolService transactionPoolService;

    @Autowired
    private IBlockRepository blockRepository;

    @Autowired
    private MiningTaskPublisher miningTaskPublisher;

    // Nivel de dificultad fijo por ahora (puede escalar después)
    private static final int DIFICULTAD = 4;

    @PostMapping("/start")
    public String iniciarMineria() {
        List<Transaction> transaccionesPendientes = transactionPoolService.obtenerTransaccionesPendientes();

        if (transaccionesPendientes.isEmpty()) {
            return "⚠️ No hay transacciones pendientes para minar.";
        }

        Iterable<Block> bloquesIterable = blockRepository.findAll();
        List<Block> bloques = new ArrayList<>();
        bloquesIterable.forEach(bloques::add);

        Block ultimoBloque = bloques.stream()
                .max(Comparator.comparing(Block::getTimestamp))
                .orElseThrow(() -> new RuntimeException("Bloque génesis no encontrado"));
        String previousHash = ultimoBloque.getBlockHash();

        // Crear tarea de minería
        MiningTask tarea = new MiningTask(previousHash, transaccionesPendientes, DIFICULTAD);

        // Publicar la tarea en RabbitMQ
        miningTaskPublisher.publishTask(tarea);

        return "🚀 Tarea de minería publicada con " + transaccionesPendientes.size() + " transacciones.";
    }
}
