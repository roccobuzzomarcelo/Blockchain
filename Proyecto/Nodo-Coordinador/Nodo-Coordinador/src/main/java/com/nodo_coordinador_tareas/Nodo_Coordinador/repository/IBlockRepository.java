package com.nodo_coordinador_tareas.Nodo_Coordinador.repository;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlockRepository extends CrudRepository<Block, String> {
}
