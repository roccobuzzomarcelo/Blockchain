package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.dto.MiningResultDTO;
import com.nodo_coordinador_tareas.Nodo_Coordinador.enums.EstadoBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.CandidateBlock;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
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
        String redisKey = "block-pending:" + resultado.getBlockId();

        CandidateBlock candidato = (CandidateBlock) redisTemplate.opsForValue().get(redisKey);

        if (candidato == null) {
            System.out.println("El bloque no existe");
            return false;
        }

        if (candidato.getEstado() == EstadoBlock.MINADO) {
            System.out.println("El bloque ya fue minado");
            return false;
        }

        String lockKey = "lock:" + resultado.getBlockId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, candidato, Duration.ofSeconds(10));

        if (locked == null || !locked) {
            System.out.println("No se pudo adquirir el lock");
            return false;
        }

        try {
            // validación del hash
            String base = candidato.getPreviousHash() + serializarTransacciones(candidato.getTransactions());
            String hashCalculado = HashUtil.calcularHashMD5(base, resultado.getNonce());
            String prefijoRequerido = "0".repeat(candidato.getDifficulty());

            if (!hashCalculado.startsWith(prefijoRequerido)) {
                System.out.println("Hash no cumple la dificultad");
                return false;
            }

            Block bloque = Block.builder()
                    .blockHash(hashCalculado)
                    .nonce(resultado.getNonce())
                    .timestamp(Instant.now())
                    .transactions(candidato.getTransactions())
                    .difficulty(candidato.getDifficulty())
                    .previousHash(candidato.getPreviousHash())
                    .build();

            blockService.save(bloque);

            // marcar como minado
            candidato.setEstado(EstadoBlock.MINADO);
            redisTemplate.opsForValue().set(redisKey, candidato, Duration.ofMinutes(10));

            return true;

        } finally {
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
