package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.repository.ITransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TransactionPoolService {

    @Autowired
    private ITransactionRepository transactionRepository;

    public Transaction guardar(Transaction tx) {
        return transactionRepository.save(tx);
    }

    public List<Transaction> obtenerTransaccionesPendientes() {
        return transactionRepository.findByEstado("PENDIENTE");
    }

    public void eliminarTransacciones(List<Transaction> transacciones) {
        transactionRepository.deleteAll(transacciones);
    }

    public void eliminarPorId(String id) {
        transactionRepository.deleteById(id);
    }
}