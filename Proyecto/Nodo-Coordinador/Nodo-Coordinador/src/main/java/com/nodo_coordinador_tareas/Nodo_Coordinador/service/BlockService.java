package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningResult;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningTask;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BlockService {

    @Autowired
    IBlockRepository blockRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    TransactionPoolService transactionPool;


    public Block save(Block block) {
        if (isValidBlock(block)) {
            block.setTimestamp(Instant.now()); // Usar tiempo actual correctamente

            Block savedBlock = blockRepository.save(block);

            // Guardar el hash del último bloque confirmado
            redisTemplate.opsForValue().set("latest_block_hash", savedBlock.getBlockHash());

            // Limpiar transacciones del pool
            transactionPool.clearTransactionsInBlock(savedBlock);

            return savedBlock;
        }
        throw new IllegalArgumentException("Bloque inválido");
    }

    private boolean isValidBlock(Block block) {
        try {
            // Concatenar info para el hash
            String datos = block.getPreviousHash()
                    + block.getTransactions().toString()
                    + block.getNonce();

            MessageDigest digest = MessageDigest.getInstance("MD5"); // Para PoW simple
            byte[] hashBytes = digest.digest(datos.getBytes(StandardCharsets.UTF_8));
            String hashCalculado = bytesToHex(hashBytes);

            // Validar dificultad
            return hashCalculado.startsWith("0".repeat(block.getDifficulty()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String getLastBlockHash() {
        Object latestHash = redisTemplate.opsForValue().get("latest_block_hash");
        return (latestHash != null && !latestHash.toString().isEmpty())
                ? latestHash.toString()
                : "0".repeat(32);
    }

    public Optional<Block> findById(String id) {
        return blockRepository.findById(id);
    }
}
