package com.nodo_coordinador_tareas.Nodo_Coordinador.listener;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.BlockManagerService;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.TransactionValidatorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionListener {

    @Autowired
    private TransactionValidatorService validator;

    private final List<Transaction> buffer = new ArrayList<>();
    private static final int BLOCK_SIZE = 5;

    @Autowired
    private BlockManagerService blockManagerService;

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE)
    public void recibirTransaccion(Transaction tx) {
        synchronized (buffer) {
            buffer.add(tx);
            System.out.println("Transacción recibida y almacenada: " + tx.getId());
        }
    }

    // Se ejecuta cada 60 segundos
    @Scheduled(fixedRate = 60000)
    public void crearBloquePorTiempo() {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                System.out.println("⏱ Creando bloque por tiempo con " + buffer.size() + " transacciones.");
                blockManagerService.crearBloqueYDispararMineria(new ArrayList<>(buffer));
                buffer.clear();
            } else {
                System.out.println("⏱ No se creó bloque por tiempo: buffer vacío.");
            }
        }
    }

}

