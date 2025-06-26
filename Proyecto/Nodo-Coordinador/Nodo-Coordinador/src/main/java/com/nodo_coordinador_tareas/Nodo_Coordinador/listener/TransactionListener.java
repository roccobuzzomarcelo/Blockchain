package com.nodo_coordinador_tareas.Nodo_Coordinador.listener;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.BlockManagerService;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.TransactionValidatorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (!validator.esValida(tx)) {
            System.out.println("Transacción inválida descartada: " + tx.getId());
            return;
        }

        synchronized (buffer) {
            buffer.add(tx);
            System.out.println("Transacción válida recibida: " + tx.getId());

            if (buffer.size() >= BLOCK_SIZE) {
                blockManagerService.crearBloqueYDispararMineria(new ArrayList<>(buffer));
                buffer.clear();
            }
        }
    }

}

