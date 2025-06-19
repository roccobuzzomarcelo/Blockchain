package com.nodo_coordinador_tareas.Nodo_Coordinador.repository;


import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, String> {

    List<Transaction> findByEstado(String estado);

}
