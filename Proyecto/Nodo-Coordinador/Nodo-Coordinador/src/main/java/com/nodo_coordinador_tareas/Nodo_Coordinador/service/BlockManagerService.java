package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningTaskDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BlockManagerService {

    @Autowired
    private BlockService blockService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public void crearBloqueYDispararMineria(List<Transaction> transacciones) {
        CandidateBlock bloque = crearBloqueCandidato(transacciones);
        // Guardar bloque candidato en Redis
        redisTemplate.opsForValue().set("block-pending:" + bloque.getCandidateId(), bloque);
        distribuirTareasDeMineria(bloque);
    }

    private CandidateBlock crearBloqueCandidato(List<Transaction> transacciones) {
        String previousHash = blockService.getLastBlockHash();
        if (previousHash == null) {
            previousHash = "0".repeat(64); // bloque génesis
        }

        return CandidateBlock.builder()
                .candidateId(UUID.randomUUID().toString())
                .previousHash(previousHash)
                .transactions(transacciones)
                .difficulty(calcularDificultad())
                .baseString(previousHash + transacciones.toString())
                .build();
    }


    private void distribuirTareasDeMineria(CandidateBlock block) {
        long totalRange = 1_000_000L;
        int partes = 10;
        long rangoPorParte = totalRange / partes;

        for (int i = 0; i < partes; i++) {
            MiningTaskDTO task = new MiningTaskDTO(
                    block.getCandidateId(),
                    block.getPreviousHash(),
                    block.getTransactions(),
                    block.getDifficulty(),
                    i * rangoPorParte,
                    (i + 1) * rangoPorParte - 1
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MINING_EXCHANGE,
                    "task.mining",
                    task
            );

            System.out.println("tareas enviadas");
        }
    }


    private int calcularDificultad() {
        // Por ahora fija, pero podrías usar lógica basada en los workers activos
        return 4;
    }
}

