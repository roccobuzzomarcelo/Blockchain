package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransactionPool {

    private final List<Transaction> pendingTransactions = new CopyOnWriteArrayList<>();

    public synchronized void addTransaction(Transaction transaction) {
        pendingTransactions.add(transaction);
    }

    public synchronized List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }

    public synchronized void removeProcessedTransactions(List<Transaction> processed) {
        pendingTransactions.removeAll(processed);
    }
}