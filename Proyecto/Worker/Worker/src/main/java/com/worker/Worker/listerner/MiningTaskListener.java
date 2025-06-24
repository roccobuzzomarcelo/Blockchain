package com.worker.Worker.listerner;

import com.worker.Worker.config.RabbitMQConfig;
import com.worker.Worker.model.MiningResult;
import com.worker.Worker.model.MiningTask;
import com.worker.Worker.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MiningTaskListener {
    @Autowired
    TaskService taskService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.TASK_QUEUE)
    public void receiveTask(MiningTask task) {
        String cadena = taskService.crearCadena(task.getPreviousHash(), task.getTransactions());
        MiningResult miningResult = taskService.minar(task, cadena);

        if (miningResult != null) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,   // El exchange donde está enlazada la cola de resultados
                    "result.mining",                // Routing key para la cola de resultados
                    miningResult
            );
            System.out.println("✅ Resultado de minería enviado a la cola");
        } else {
            System.out.println("❌ No se encontró solución en el rango asignado");
        }
    }
}
