package com.nodo_coordinador_tareas.Nodo_Coordinador.config;

import com.nodo_coordinador_tareas.Nodo_Coordinador.model.MiningTask;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MiningTaskPublisher {

    private final RabbitTemplate rabbitTemplate;

    public MiningTaskPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTask(MiningTask task) {
        rabbitTemplate.convertAndSend("mining_tasks_queue", task);
        System.out.println("Tarea de minería publicada: " + task);
    }
}

