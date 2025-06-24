package com.worker.Worker.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange común para enrutar tareas y resultados
    public static final String EXCHANGE_NAME = "mining_exchange";

    // Colas
    public static final String TASK_QUEUE = "mining_tasks_queue";
    public static final String RESULT_QUEUE = "mining_results_queue";

    // Routing Keys
    public static final String TASK_ROUTING_KEY = "task.mining";
    public static final String RESULT_ROUTING_KEY = "result.mining";

    // Exchange tipo Direct (enruta por clave exacta)
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue taskQueue() {
        return new Queue(TASK_QUEUE, true); // true = durable
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE, true);
    }

    // Enlazar taskQueue con el exchange usando "task.mining"
    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange exchange) {
        return BindingBuilder.bind(taskQueue).to(exchange).with(TASK_ROUTING_KEY);
    }

    // Enlazar resultQueue con el exchange usando "result.mining"
    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange exchange) {
        return BindingBuilder.bind(resultQueue).to(exchange).with(RESULT_ROUTING_KEY);
    }

    // Bean para enviar mensajes (desde cualquier servicio)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}


