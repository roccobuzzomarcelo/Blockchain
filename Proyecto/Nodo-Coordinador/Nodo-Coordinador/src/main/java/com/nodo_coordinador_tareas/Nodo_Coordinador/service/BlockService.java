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

import java.util.List;
import java.util.Optional;

@Service
public class BlockService implements IBlockService{

    @Autowired
    IBlockRepository blockRepository;

    public Block save(Block block) {
        return blockRepository.save(block);
    }

    public Optional<Block> findById(String id) {
        return blockRepository.findById(id);
    }


}