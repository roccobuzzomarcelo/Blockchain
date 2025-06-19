package com.nodo_coordinador_tareas.Nodo_Coordinador.service;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningTask;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransactionPoolService {
    // Almacenamiento en memoria (más eficiente para un pool temporal)
    private final List<Transaction> pendingTransactions = new CopyOnWriteArrayList<>();

    @Autowired
    private BlockService blockchainService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public synchronized void addTransaction(Transaction tx) {
        if (isValid(tx) && !isDuplicate(tx)) {
            pendingTransactions.add(tx);
            checkForBlockCreation();
        }
    }

    public List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }

    private boolean isValid(Transaction tx) {
        if (tx == null) return false;

        boolean camposCompletos = tx.getUsuarioEmisor() != null && !tx.getUsuarioEmisor().isEmpty()
                && tx.getUsuarioReceptor() != null && !tx.getUsuarioReceptor().isEmpty();

        boolean montoValido = tx.getMonto() > 0;

        return camposCompletos && montoValido;
    }

    private boolean isDuplicate(Transaction tx) {
        return pendingTransactions.stream()
                .anyMatch(t -> t.getId().equals(tx.getId()));
    }



    private void checkForBlockCreation() {
        int minTxPerBlock = 5;
        if (pendingTransactions.size() >= minTxPerBlock) {
            Block block = createBlock(); // crea bloque con transacciones + dificultad
            MiningTask task = new MiningTask(
                    block.getPreviousHash(),
                    block.getTransactions(),
                    block.getDifficulty()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "task.mining",
                    task
            );
        }
    }

    public Block createBlock() {
        List<Transaction> blockTransactions = new ArrayList<>();
        int maxTxPerBlock = 5; // Podés ajustar este valor

        // Extraer hasta 5 transacciones del pool
        while (!pendingTransactions.isEmpty() && blockTransactions.size() < maxTxPerBlock) {
            blockTransactions.add(pendingTransactions.remove(0));
        }

        // Obtener el hash del último bloque (si no hay, usar valor inicial)
        String previousHash = blockchainService.getLastBlockHash();
        if (previousHash == null || previousHash.isEmpty()) {
            previousHash = "0".repeat(64); // hash inicial (bloque génesis)
        }

        // Definir la dificultad del PoW
        int dificultad = calculateDifficulty(); // Podés ajustar este método

        // Crear bloque sin hash aún (será minado)
        return Block.builder()
                .previousHash(previousHash)
                .transactions(blockTransactions)
                .difficulty(dificultad)
                .build();
    }

    private int calculateDifficulty() {
        return 4;
    }


    public void clearTransactionsInBlock(Block savedBlock) {
        List<Transaction> transaccionesEnBloque = savedBlock.getTransactions();
        // Remover del pool todas las transacciones que ya fueron incluidas en el bloque
        pendingTransactions.removeIf(transaccionesEnBloque::contains);
    }

}