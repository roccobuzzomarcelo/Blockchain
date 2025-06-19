package com.nodo_coordinador_tareas.Nodo_Coordinador.listener;

import com.nodo_coordinador_tareas.Nodo_Coordinador.config.RabbitMQConfig;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Block;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningResult;
import com.nodo_coordinador_tareas.Nodo_Coordinador.model.Transaction;
import com.nodo_coordinador_tareas.Nodo_Coordinador.service.BlockService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

@Component
public class MiningResultListener {

    @Autowired
    private BlockService blockService;

    @RabbitListener(queues = RabbitMQConfig.RESULT_QUEUE)
    public void recibirResultado(MiningResult result) {
        Block nuevoBloque = new Block(
                result.getBlockHash(),
                result.getPreviousHash(),
                result.getNonce(),
                Instant.now(),
                result.getTransactions(),
                4 // o la dificultad del bloque, según la lógica
        );

        try {
            blockService.save(nuevoBloque);
            System.out.println("Bloque aceptado y guardado: " + result.getBlockHash());
        } catch (Exception e) {
            System.out.println("Resultado inválido: " + e.getMessage());
        }
    }
}
