package com.nodo_coordinador_tareas.Nodo_Coordinador.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "mining_tasks_exchange";
    public static final String TASK_QUEUE = "mining_tasks_queue";
    public static final String RESULT_QUEUE = "mining_results_queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue taskQueue() {
        return new Queue(TASK_QUEUE);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE);
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, TopicExchange exchange) {
        return BindingBuilder.bind(taskQueue).to(exchange).with("task.#");
    }
}