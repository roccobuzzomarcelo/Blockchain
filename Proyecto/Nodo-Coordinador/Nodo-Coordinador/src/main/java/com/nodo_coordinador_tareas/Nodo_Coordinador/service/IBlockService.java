package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.IBlockRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface IBlockService {

    public Block save(Block block);

    public Optional<Block> findById(String id);
}
