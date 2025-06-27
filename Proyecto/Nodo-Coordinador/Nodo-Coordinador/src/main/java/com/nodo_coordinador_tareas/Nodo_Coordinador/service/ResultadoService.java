package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ResultadoService {


    @Autowired
    private RedisTemplate<String,CandidateBlock> redisTemplate;


    private final AtomicLong altura = new AtomicLong(0);

    @Autowired
    private BlockService blockService;

    public boolean procesarResultado(MiningResultDTO resultado) {
        // 1. Adquirir un lock distribuido para evitar race conditions
        String redisKey = "block-pending:" + resultado.getBlockId();
        CandidateBlock candidato = redisTemplate.opsForValue().get(redisKey);
        String lockKey = "lock:" + resultado.getBlockId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey, candidato ,
                Duration.ofSeconds(10)  // Lock expira en 10 segundos (ajusta según necesidad)
        );

        if (locked == null || !locked) {
            System.out.println("No se pudo adquirir el lock para el bloque " + resultado.getBlockId());
            return false; // Otro worker ya está procesando este bloque
        }

        try {
            // 2. Verificar si el bloque aún está pendiente en Redis

            if (candidato == null) {
                System.out.println("Bloque ya fue minado o no existe " +  resultado.getBlockId());
                return false;
            }

            String base = candidato.getPreviousHash()
                    + serializarTransacciones(candidato.getTransactions());
            // 3. Validar el hash (ej: que empiece con "00000")
            String hashCalculado = HashUtil.calcularHashMD5(base,resultado.getNonce());
            String prefijoRequerido = "0".repeat(candidato.getDifficulty());

            if (!hashCalculado.startsWith(prefijoRequerido)) {
                System.out.println("Hash no cumple la dificultad requerida: " + hashCalculado);
                return false;
            }

            // 4. Si todo es válido, eliminar el bloque de Redis (ya minado)
            System.out.println("el bloque original fue creado con exito!");
            Block bloque = Block.builder()
                    .blockHash(hashCalculado)
                    .nonce(resultado.getNonce())
                    .timestamp(java.time.Instant.now())
                    .transactions(candidato.getTransactions())
                    .difficulty(prefijoRequerido.length())
                    .previousHash(candidato.getPreviousHash())
                    .build();

            blockService.save(bloque);
            redisTemplate.delete(redisKey);
            return true;

        } finally {
            // 5. Liberar el lock SIEMPRE (incluso si hay errores)
            redisTemplate.delete(lockKey);
        }
    }

    /*public boolean procesarResultado(MiningResultDTO resultado) {
        System.out.println("Empezando a vlaidar ...");
        CandidateBlock candidato = redisTemplate.opsForValue().get("block-pending:" + resultado.getBlockId());

        if (candidato == null){
            System.out.println("candidto nulo");
            return false;
        }

        String base = candidato.getPreviousHash()
                + serializarTransacciones(candidato.getTransactions());

        String hashCalculado = HashUtil.calcularHashMD5(base, resultado.getNonce());

        String targetPrefix = "0".repeat(candidato.getDifficulty());

        System.out.println("El hash calculado coordinador: " +  hashCalculado);
        System.out.println("El hash calculado worker: " + resultado.getHash());

        if (hashCalculado.equals(resultado.getHash()) && hashCalculado.startsWith(targetPrefix)) {
            Block bloque = Block.builder()
                    .blockHash(hashCalculado)
                    .nonce(resultado.getNonce())
                    .timestamp(java.time.Instant.now())
                    .transactions(candidato.getTransactions())
                    .difficulty(targetPrefix.length())
                    .previousHash(candidato.getPreviousHash())
                    .build();

            blockService.save(bloque);
            redisTemplate.delete("block-pending:" + resultado.getBlockId());
            return true;
        }

        return false;
    }*/


    private String serializarTransacciones(ArrayList<Transaction> txs) {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : txs) {
            sb.append(tx.getId())
                    .append(tx.getUsuarioEmisor())
                    .append(tx.getUsuarioReceptor())
                    .append(tx.getMonto());
        }
        return sb.toString();
    }
}
