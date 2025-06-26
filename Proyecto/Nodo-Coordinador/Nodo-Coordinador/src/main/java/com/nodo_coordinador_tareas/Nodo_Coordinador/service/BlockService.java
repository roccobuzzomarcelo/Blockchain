package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class BlockService {

    @Autowired
    IBlockRepository blockRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public Block save(Block block) {
        block.setTimestamp(Instant.now()); // Usar tiempo actual correctamente

        Block savedBlock = blockRepository.save(block);

        // Guardar el hash del último bloque confirmado
        redisTemplate.opsForValue().set("latest_block_hash", savedBlock.getBlockHash());

        return savedBlock;
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
