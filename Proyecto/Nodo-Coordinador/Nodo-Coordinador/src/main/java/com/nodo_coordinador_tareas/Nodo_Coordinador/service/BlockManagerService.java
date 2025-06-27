package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningTaskDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BlockManagerService {

    @Autowired
    private BlockService blockService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    @Qualifier("candidateBlockRedisTemplate")
    private RedisTemplate<String,CandidateBlock> redisTemplate;


    public void guardarCandidateBlock(CandidateBlock block) {
        String key = "block-pending:" + block.getCandidateId();
        redisTemplate.opsForValue().set(key, block);
    }

    public CandidateBlock obtenerCandidateBlock(String blockId) {
        String key = "block-pending:" + blockId;
        return redisTemplate.opsForValue().get(key);
    }

    public void crearBloqueYDispararMineria(ArrayList<Transaction> transacciones) {
        CandidateBlock bloque = crearBloqueCandidato(transacciones);
        // Guardar bloque candidato en Redis
        redisTemplate.opsForValue().set("block-pending:" + bloque.getCandidateId(), bloque);
        distribuirTareasDeMineria(bloque);
    }

    private CandidateBlock crearBloqueCandidato(ArrayList<Transaction> transacciones) {
        String previousHash = blockService.getLastBlockHash();
        if (previousHash == null) {
            previousHash = "0".repeat(64); // bloque génesis
        }

        CandidateBlock candidateBlock = CandidateBlock.builder()
                .candidateId(UUID.randomUUID().toString())
                .previousHash(previousHash)
                .transactions(transacciones)
                .estado(EstadoBlock.PENDIENTE)
                .difficulty(calcularDificultad())
                .build();

        System.out.println("id bloque candidato: " + candidateBlock.getCandidateId());
        guardarCandidateBlock(candidateBlock);
        return candidateBlock;


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
        return 1;
    }
}

