package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidatorService {

    /**
     * Valida que una transacción tenga todos los datos requeridos y coherentes.
     */
    public boolean esValida(Transaction tx) {
        if (tx == null) return false;

        if (tx.getUsuarioEmisor() == null || tx.getUsuarioEmisor().isEmpty()) return false;
        if (tx.getUsuarioReceptor() == null || tx.getUsuarioReceptor().isEmpty()) return false;
        if (tx.getMonto() <= 0) return false;

        return true;
    }

    /**
     * Verifica si dos transacciones tienen el mismo ID (para evitar duplicados)
     */
    public boolean esDuplicada(Transaction tx1, Transaction tx2) {
        return tx1 != null && tx2 != null && tx1.getId().equals(tx2.getId());
    }
}
