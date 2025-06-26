package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningTask;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockManagerService {

    @Autowired
    private BlockService blockService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void crearBloqueYDispararMineria(List<Transaction> transacciones) {
        Block bloque = crearBloque(transacciones);
        distribuirTareasDeMineria(bloque);
    }

    private Block crearBloque(List<Transaction> transacciones) {
        String previousHash = blockService.getLastBlockHash();
        if (previousHash == null) {
            previousHash = "0".repeat(64);
        }

        return Block.builder()
                .previousHash(previousHash)
                .transactions(transacciones)
                .difficulty(calcularDificultad())
                .build();
    }

    private void distribuirTareasDeMineria(Block block) {
        long totalRange = 1_000_000L;
        int partes = 10;
        long rangoPorParte = totalRange / partes;

        for (int i = 0; i < partes; i++) {
            MiningTask task = new MiningTask(
                    block.getPreviousHash(),
                    block.getTransactions(),
                    block.getDifficulty(),
                    i * rangoPorParte,
                    (i + 1) * rangoPorParte - 1
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "task.mining",
                    task
            );
        }
    }

    private int calcularDificultad() {
        // Por ahora fija, pero podrías usar lógica basada en los workers activos
        return 4;
    }
}

